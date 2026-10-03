package com.ifpb.notificacoes.service;

import com.ifpb.notificacoes.dto.NotificacaoFiltroDTO;
import com.ifpb.notificacoes.dto.NotificacaoResponseDTO;
import com.ifpb.notificacoes.dto.PaginaDTO;
import com.ifpb.notificacoes.model.Notificacao;
import com.ifpb.notificacoes.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoServiceDuplicadasTest {

    private static final LocalDate NASCIMENTO = LocalDate.of(1990, 5, 20);

    private NotificacaoRepository repository;
    private NotificacaoService service;

    @BeforeEach
    void preparar() {
        repository = new NotificacaoRepository();
        service = new NotificacaoService(repository);
    }

    private Long salvar(String agravo, String paciente, LocalDate nascimento, String mae, LocalDate notificacao) {

        Notificacao n = new Notificacao();
        n.setAgravo(agravo);
        n.setNomePaciente(paciente);
        n.setDataNascimento(nascimento);
        n.setNomeMae(mae);
        n.setDataNotificacao(notificacao);
        n.setUfNotificacao("PB");
        n.setMunicipioNotificacao("Cajazeiras");

        return repository.salvar(n).getId();
    }

    private Long salvarDengue(String paciente, String mae, LocalDate notificacao) {
        return salvar("Dengue", paciente, NASCIMENTO, mae, notificacao);
    }

    private NotificacaoFiltroDTO duplicadas() {
        NotificacaoFiltroDTO filtro = new NotificacaoFiltroDTO();
        filtro.setDuplicadas(true);
        return filtro;
    }

    private List<Long> ids(PaginaDTO<NotificacaoResponseDTO> pagina) {
        return pagina.getConteudo().stream().map(NotificacaoResponseDTO::getId).sorted().toList();
    }

    @Test
    void duasNotificacoesIguaisComDoisDiasDeDiferencaSaoDuplicadas() {
        Long a = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 10));
        Long b = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 12));

        assertEquals(List.of(a, b), ids(service.listar(duplicadas())));
    }

    @Test
    void tresDiasDeDiferencaAindaContamComoDuplicada() {
        Long a = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 10));
        Long b = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 13));

        assertEquals(List.of(a, b), ids(service.listar(duplicadas())));
    }

    @Test
    void quatroDiasDeDiferencaNaoSaoDuplicadas() {
        salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 10));
        salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 14));

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(duplicadas());

        assertTrue(pagina.getConteudo().isEmpty());
        assertEquals(0L, pagina.getTotal());
    }

    @Test
    void ordemDasDatasNaoImporta() {
        Long recente = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 12));
        Long antiga = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 10));

        assertEquals(List.of(recente, antiga), ids(service.listar(duplicadas())));
    }

    @Test
    void pacienteEMaeIgnoramMaiusculasEEspacosExtras() {
        Long a = salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 10));
        Long b = salvarDengue("  MARIA   da  silva ", " ana DA   Silva  ", LocalDate.of(2026, 3, 11));

        assertEquals(List.of(a, b), ids(service.listar(duplicadas())));
    }

    @Test
    void agravoTambemIgnoraMaiusculasEEspacosNasPontas() {
        Long a = salvar("Dengue", "Maria da Silva", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));
        Long b = salvar("  dengue ", "Maria da Silva", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 11));

        assertEquals(List.of(a, b), ids(service.listar(duplicadas())));
    }

    @Test
    void agravosDiferentesNaoSaoDuplicadas() {
        salvar("Dengue", "Maria da Silva", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));
        salvar("Zika", "Maria da Silva", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));

        assertEquals(0L, service.listar(duplicadas()).getTotal());
    }

    @Test
    void pacientesDiferentesNaoSaoDuplicadas() {
        salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 10));
        salvarDengue("Maria de Souza", "Ana", LocalDate.of(2026, 3, 10));

        assertEquals(0L, service.listar(duplicadas()).getTotal());
    }

    @Test
    void datasDeNascimentoDiferentesNaoSaoDuplicadas() {
        salvar("Dengue", "Maria da Silva", LocalDate.of(1990, 5, 20), "Ana", LocalDate.of(2026, 3, 10));
        salvar("Dengue", "Maria da Silva", LocalDate.of(1991, 5, 20), "Ana", LocalDate.of(2026, 3, 10));

        assertEquals(0L, service.listar(duplicadas()).getTotal());
    }

    @Test
    void maesDiferentesNaoSaoDuplicadas() {
        salvarDengue("Maria da Silva", "Ana da Silva", LocalDate.of(2026, 3, 10));
        salvarDengue("Maria da Silva", "Joana Pereira", LocalDate.of(2026, 3, 10));

        assertEquals(0L, service.listar(duplicadas()).getTotal());
    }

    @Test
    void maeVaziaOuNulaNuncaCasaComOutraMaeVazia() {
        salvarDengue("Maria da Silva", null, LocalDate.of(2026, 3, 10));
        salvarDengue("Maria da Silva", "   ", LocalDate.of(2026, 3, 10));
        salvarDengue("Maria da Silva", "", LocalDate.of(2026, 3, 10));

        assertEquals(0L, service.listar(duplicadas()).getTotal());
    }

    @Test
    void registrosSemDadosObrigatoriosSaoIgnoradosSemErro() {
        salvar("Dengue", "Maria da Silva", null, "Ana", LocalDate.of(2026, 3, 10));
        salvar("Dengue", "Maria da Silva", null, "Ana", LocalDate.of(2026, 3, 10));
        salvar("Dengue", "Maria da Silva", NASCIMENTO, "Ana", null);
        salvar("Dengue", "Maria da Silva", NASCIMENTO, "Ana", null);
        salvar(null, "Maria da Silva", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));
        salvar(null, "Maria da Silva", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));
        salvar("Dengue", null, NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));
        salvar("Dengue", " ", NASCIMENTO, "Ana", LocalDate.of(2026, 3, 10));

        assertEquals(0L, service.listar(duplicadas()).getTotal());
    }

    @Test
    void cadeiaDeNotificacoesConsecutivasMarcaTodasComoDuplicadas() {
        // 1 -> 4 -> 7 de março: cada par vizinho tem 3 dias, embora a primeira e a última estejam a 6.
        Long a = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 1));
        Long b = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 4));
        Long c = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 7));

        assertEquals(List.of(a, b, c), ids(service.listar(duplicadas())));
    }

    @Test
    void registroDistanteDoGrupoNaoEhMarcadoJuntoComOsDuplicados() {
        Long a = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 10));
        Long b = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 11));
        salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 6, 1));

        assertEquals(List.of(a, b), ids(service.listar(duplicadas())));
    }

    @Test
    void gruposDeDuplicadasDiferentesSaoRetornadosJuntos() {
        Long a1 = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 10));
        Long a2 = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 11));
        Long b1 = salvarDengue("João Souza", "Rita", LocalDate.of(2026, 4, 1));
        Long b2 = salvarDengue("João Souza", "Rita", LocalDate.of(2026, 4, 2));
        salvarDengue("Carla Dias", "Lia", LocalDate.of(2026, 5, 1));

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(duplicadas());

        assertEquals(List.of(a1, a2, b1, b2), ids(pagina));
        assertEquals(4L, pagina.getTotal());
    }

    @Test
    void duplicadasFalsoOuAusenteNaoFiltraNada() {
        salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 10));
        salvarDengue("João Souza", "Rita", LocalDate.of(2026, 4, 1));

        NotificacaoFiltroDTO falso = new NotificacaoFiltroDTO();
        falso.setDuplicadas(false);

        assertEquals(2L, service.listar(falso).getTotal());
        assertEquals(2L, service.listar(new NotificacaoFiltroDTO()).getTotal());
    }

    @Test
    void duplicadasCombinaComOsDemaisFiltrosSemPerderOParForaDoPeriodo() {
        Long dentro = salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 11));
        salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 9));
        salvarDengue("João Souza", "Rita", LocalDate.of(2026, 3, 11));
        salvarDengue("João Souza", "Rita", LocalDate.of(2026, 3, 12));

        NotificacaoFiltroDTO filtro = duplicadas();
        filtro.setDataNotificacaoDe(LocalDate.of(2026, 3, 11));
        filtro.setNomePaciente("maria");

        // A notificação de 9/3 fica fora do período, mas é ela que torna a de 11/3 uma duplicata.
        assertEquals(List.of(dentro), ids(service.listar(filtro)));
    }

    @Test
    void duplicadasRespeitaPaginacaoEOrdenacao() {
        salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 10));
        salvarDengue("Maria da Silva", "Ana", LocalDate.of(2026, 3, 11));
        salvarDengue("João Souza", "Rita", LocalDate.of(2026, 4, 1));
        salvarDengue("João Souza", "Rita", LocalDate.of(2026, 4, 2));

        NotificacaoFiltroDTO filtro = duplicadas();
        filtro.setTamanho(3);
        filtro.setPagina(2);
        filtro.setOrdenarPor("id");
        filtro.setOrdem("ASC");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(filtro);

        assertEquals(List.of(4L), ids(pagina));
        assertEquals(4L, pagina.getTotal());
        assertEquals(2, pagina.getTotalPaginas());
        assertEquals(2, pagina.getPagina());
    }
}
