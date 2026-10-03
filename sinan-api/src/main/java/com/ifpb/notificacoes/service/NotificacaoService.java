package com.ifpb.notificacoes.service;

import com.ifpb.notificacoes.dto.NotificacaoFiltroDTO;
import com.ifpb.notificacoes.dto.NotificacaoRequestDTO;
import com.ifpb.notificacoes.dto.NotificacaoResponseDTO;
import com.ifpb.notificacoes.dto.PaginaDTO;
import com.ifpb.notificacoes.dto.ResidenciaDTO;
import com.ifpb.notificacoes.exception.NotificacaoNaoEncontradaException;
import com.ifpb.notificacoes.model.Notificacao;
import com.ifpb.notificacoes.model.Residencia;
import com.ifpb.notificacoes.repository.NotificacaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class NotificacaoService {

    private static final Comparator<String> TEXTO = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

    private static final Comparator<LocalDate> DATA = Comparator.nullsLast(Comparator.naturalOrder());

    private static final Comparator<Long> NUMERO = Comparator.nullsLast(Comparator.naturalOrder());

    /** Campos que o front-end pode usar na ordenação, com o comparador de cada um. */
    private static final Map<String, Comparator<Notificacao>> ORDENS = Map.of(
            "id", Comparator.comparing(Notificacao::getId, NUMERO),
            "agravo", Comparator.comparing(Notificacao::getAgravo, TEXTO),
            "nomePaciente", Comparator.comparing(Notificacao::getNomePaciente, TEXTO),
            "municipioNotificacao", Comparator.comparing(Notificacao::getMunicipioNotificacao, TEXTO),
            "dataNotificacao", Comparator.comparing(Notificacao::getDataNotificacao, DATA)
    );

    /** Diferença máxima, em dias, entre as datas de notificação de duas ocorrências duplicadas. */
    static final int DIAS_MAXIMOS_ENTRE_DUPLICADAS = 3;

    private static final Pattern ESPACOS = Pattern.compile("\\s+");

    /** Dados que precisam coincidir para duas notificações serem candidatas a duplicadas. */
    private record ChaveDuplicidade(String agravo, String paciente, LocalDate dataNascimento, String mae) {
    }

    private final NotificacaoRepository repository;

    public NotificacaoService(NotificacaoRepository repository) {
        this.repository = repository;
    }

    public NotificacaoResponseDTO criar(NotificacaoRequestDTO dto) {

        Notificacao notificacao = paraEntidade(dto);
        notificacao.setId(null);

        return paraResposta(repository.salvar(notificacao));
    }

    public NotificacaoResponseDTO atualizar(Long id, NotificacaoRequestDTO dto) {

        repository.buscarPorId(id)
                .orElseThrow(() -> new NotificacaoNaoEncontradaException(id));

        Notificacao notificacao = paraEntidade(dto);
        notificacao.setId(id);

        return paraResposta(repository.salvar(notificacao));
    }

    public void excluir(Long id) {

        boolean removida = repository.excluirPorId(id);

        if (!removida) {
            throw new NotificacaoNaoEncontradaException(id);
        }
    }

    public PaginaDTO<NotificacaoResponseDTO> listar(NotificacaoFiltroDTO filtro) {

        List<Notificacao> candidatas = repository.listarTodos();

        // A duplicidade é apurada sobre todos os registros; os demais filtros só reduzem o resultado.
        // Assim, uma duplicata não some porque o par dela ficou fora do período ou do município filtrado.
        if (filtro.isBuscarDuplicadas()) {
            candidatas = somenteDuplicadas(candidatas);
        }

        List<Notificacao> filtradas = candidatas.stream()
                .filter(notificacao -> corresponde(notificacao, filtro))
                .sorted(ordenacao(filtro))
                .toList();

        long total = filtradas.size();
        long deslocamento = Math.max(0L, ((long) filtro.getPagina() - 1) * filtro.getTamanho());
        int inicio = (int) Math.min(deslocamento, total);
        int fim = (int) Math.min((long) inicio + filtro.getTamanho(), total);

        List<NotificacaoResponseDTO> conteudo = filtradas.subList(inicio, fim).stream()
                .map(this::paraResposta)
                .toList();

        return new PaginaDTO<>(conteudo, filtro.getPagina(), filtro.getTamanho(), total);
    }

    public NotificacaoResponseDTO paraResposta(Notificacao notificacao) {

        NotificacaoResponseDTO resposta = new NotificacaoResponseDTO();
        BeanUtils.copyProperties(notificacao, resposta);

        if (notificacao.getResidencia() != null) {
            ResidenciaDTO residencia = new ResidenciaDTO();
            BeanUtils.copyProperties(notificacao.getResidencia(), residencia);
            resposta.setResidencia(residencia);
        }

        return resposta;
    }

    private Notificacao paraEntidade(NotificacaoRequestDTO dto) {

        Notificacao notificacao = new Notificacao();
        BeanUtils.copyProperties(dto, notificacao);

        if (dto.getResidencia() != null) {
            Residencia residencia = new Residencia();
            BeanUtils.copyProperties(dto.getResidencia(), residencia);
            notificacao.setResidencia(residencia);
        }

        return notificacao;
    }

    private boolean corresponde(Notificacao notificacao, NotificacaoFiltroDTO filtro) {

        return igual(filtro.getAgravo(), notificacao.getAgravo())
                && igual(filtro.getUfNotificacao(), notificacao.getUfNotificacao())
                && contem(filtro.getMunicipioNotificacao(), notificacao.getMunicipioNotificacao())
                && contem(filtro.getNomePaciente(), notificacao.getNomePaciente())
                && dentroDoPeriodo(notificacao.getDataNotificacao(), filtro);
    }

    private Comparator<Notificacao> ordenacao(NotificacaoFiltroDTO filtro) {

        Comparator<Notificacao> comparador = ORDENS.getOrDefault(
                filtro.getOrdenarPor(),
                ORDENS.get(NotificacaoFiltroDTO.ORDENAR_POR_PADRAO)
        );

        return "asc".equalsIgnoreCase(filtro.getOrdem())
                ? comparador
                : comparador.reversed();
    }

    /**
     * Mantém só as notificações que têm ao menos uma duplicata, isto é, outra notificação com o mesmo
     * agravo, paciente, data de nascimento e nome da mãe, e data de notificação a no máximo
     * {@value #DIAS_MAXIMOS_ENTRE_DUPLICADAS} dias de distância.
     *
     * <p>Agravo, paciente e mãe são comparados sem diferenciar maiúsculas de minúsculas e sem espaços
     * sobrando. Registros sem algum desses dados (ou sem datas) nunca são considerados duplicados,
     * porque não há base para afirmar que se trata da mesma pessoa.
     */
    private List<Notificacao> somenteDuplicadas(List<Notificacao> notificacoes) {

        Map<ChaveDuplicidade, List<Notificacao>> grupos = new HashMap<>();

        for (Notificacao notificacao : notificacoes) {
            ChaveDuplicidade chave = chaveDeDuplicidade(notificacao);

            if (chave != null) {
                grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(notificacao);
            }
        }

        Set<Notificacao> duplicadas = Collections.newSetFromMap(new IdentityHashMap<>());

        for (List<Notificacao> grupo : grupos.values()) {

            if (grupo.size() < 2) {
                continue;
            }

            // Com o grupo ordenado por data, o vizinho mais próximo de cada registro é sempre um dos
            // dois adjacentes; basta comparar cada par consecutivo.
            grupo.sort(Comparator.comparing(Notificacao::getDataNotificacao));

            for (int i = 1; i < grupo.size(); i++) {
                Notificacao anterior = grupo.get(i - 1);
                Notificacao atual = grupo.get(i);

                long dias = ChronoUnit.DAYS.between(anterior.getDataNotificacao(), atual.getDataNotificacao());

                if (dias <= DIAS_MAXIMOS_ENTRE_DUPLICADAS) {
                    duplicadas.add(anterior);
                    duplicadas.add(atual);
                }
            }
        }

        return notificacoes.stream()
                .filter(duplicadas::contains)
                .toList();
    }

    private ChaveDuplicidade chaveDeDuplicidade(Notificacao notificacao) {

        String agravo = normalizar(notificacao.getAgravo());
        String paciente = normalizar(notificacao.getNomePaciente());
        String mae = normalizar(notificacao.getNomeMae());

        if (agravo == null || paciente == null || mae == null
                || notificacao.getDataNascimento() == null
                || notificacao.getDataNotificacao() == null) {
            return null;
        }

        return new ChaveDuplicidade(agravo, paciente, notificacao.getDataNascimento(), mae);
    }

    /** Remove espaços das pontas e repetidos no meio e ignora maiúsculas; texto vazio vira {@code null}. */
    private String normalizar(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return ESPACOS.matcher(texto.trim()).replaceAll(" ").toLowerCase(Locale.ROOT);
    }

    private boolean igual(String filtro, String valor) {

        if (filtro == null || filtro.isBlank()) {
            return true;
        }

        return valor != null && valor.trim().equalsIgnoreCase(filtro.trim());
    }

    private boolean contem(String filtro, String valor) {

        if (filtro == null || filtro.isBlank()) {
            return true;
        }

        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(filtro.trim().toLowerCase(Locale.ROOT));
    }

    private boolean dentroDoPeriodo(LocalDate data, NotificacaoFiltroDTO filtro) {

        if (filtro.getDataNotificacaoDe() == null && filtro.getDataNotificacaoAte() == null) {
            return true;
        }

        if (data == null) {
            return false;
        }

        if (filtro.getDataNotificacaoDe() != null && data.isBefore(filtro.getDataNotificacaoDe())) {
            return false;
        }

        return filtro.getDataNotificacaoAte() == null || !data.isAfter(filtro.getDataNotificacaoAte());
    }
}