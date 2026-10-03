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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        List<Notificacao> filtradas = repository.listarTodos().stream()
                .filter(notificacao -> corresponde(notificacao, filtro))
                .sorted(ordenacao(filtro))
                .toList();

        long total = filtradas.size();
        int inicio = (int) Math.min((long) filtro.getPagina() * filtro.getTamanho(), total);
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

        return "asc".equalsIgnoreCase(filtro.getDirecao())
                ? comparador
                : comparador.reversed();
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