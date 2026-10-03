package com.ifpb.notificacoes.service;

import com.ifpb.notificacoes.dto.NotificacaoFiltroDTO;
import com.ifpb.notificacoes.dto.NotificacaoResponseDTO;
import com.ifpb.notificacoes.dto.PaginaDTO;
import com.ifpb.notificacoes.model.Notificacao;
import com.ifpb.notificacoes.model.Residencia;
import com.ifpb.notificacoes.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoServiceListagemTest {

    private NotificacaoRepository repository;
    private NotificacaoService service;

    @BeforeEach
    void preparar() {
        repository = new NotificacaoRepository();
        service = new NotificacaoService(repository);

        // 1 Dengue/PB/Cajazeiras/Maria da Silva/2026-03-12
        repository.salvar(caso("Dengue", "PB", "Cajazeiras", "Maria da Silva", LocalDate.of(2026, 3, 12)));
        // 2 Dengue/PB/Sousa/João Souza/2026-01-05
        repository.salvar(caso("Dengue", "PB", "Sousa", "João Souza", LocalDate.of(2026, 1, 5)));
        // 3 Dengue/PE/Recife/Carla Dias/2026-06-20
        repository.salvar(caso("Dengue", "PE", "Recife", "Carla Dias", LocalDate.of(2026, 6, 20)));
        // 4 Zika/PB/Cajazeiras/Maria Souza/2026-02-10
        repository.salvar(caso("Zika", "PB", "Cajazeiras", "Maria Souza", LocalDate.of(2026, 2, 10)));
        // 5 Zika/PB/Pombal/José Lima/2026-05-18
        repository.salvar(caso("Zika", "PB", "Pombal", "José Lima", LocalDate.of(2026, 5, 18)));
        // 6 Chikungunya/PE/Olinda/Beatriz Rocha/2026-04-02
        repository.salvar(caso("Chikungunya", "PE", "Olinda", "Beatriz Rocha", LocalDate.of(2026, 4, 2)));
    }

    private Notificacao caso(String agravo, String uf, String municipio, String nome, LocalDate data) {

        Notificacao notificacao = new Notificacao();
        notificacao.setAgravo(agravo);
        notificacao.setUfNotificacao(uf);
        notificacao.setMunicipioNotificacao(municipio);
        notificacao.setNomePaciente(nome);
        notificacao.setDataNotificacao(data);

        Residencia residencia = new Residencia();
        residencia.setUf(uf);
        residencia.setMunicipio(municipio);
        notificacao.setResidencia(residencia);

        return notificacao;
    }

    private NotificacaoFiltroDTO filtro() {
        return new NotificacaoFiltroDTO();
    }

    private List<String> nomes(PaginaDTO<NotificacaoResponseDTO> pagina) {
        return pagina.getConteudo().stream().map(NotificacaoResponseDTO::getNomePaciente).toList();
    }

    private List<Long> ids(PaginaDTO<NotificacaoResponseDTO> pagina) {
        return pagina.getConteudo().stream().map(NotificacaoResponseDTO::getId).toList();
    }

    @Test
    void semFiltrosRetornaTudoDaDataMaisRecenteParaAMaisAntiga() {
        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(filtro());

        assertEquals(List.of("Carla Dias", "José Lima", "Beatriz Rocha", "Maria da Silva", "Maria Souza", "João Souza"),
                nomes(pagina));
        assertEquals(6L, pagina.getTotal());
        assertEquals(1, pagina.getPagina());
        assertEquals(10, pagina.getTamanho());
        assertEquals(1, pagina.getTotalPaginas());
    }

    @Test
    void paginacaoDivideOResultadoEInformaOTotal() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setTamanho(2);
        dto.setPagina(1);

        PaginaDTO<NotificacaoResponseDTO> primeira = service.listar(dto);

        assertEquals(List.of("Carla Dias", "José Lima"), nomes(primeira));
        assertEquals(6L, primeira.getTotal());
        assertEquals(3, primeira.getTotalPaginas());

        dto.setPagina(3);
        PaginaDTO<NotificacaoResponseDTO> ultima = service.listar(dto);

        assertEquals(List.of("Maria Souza", "João Souza"), nomes(ultima));
        assertEquals(6L, ultima.getTotal());
    }

    @Test
    void segundaPaginaComecaDepoisDosItensDaPrimeira() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setTamanho(4);
        dto.setPagina(2);

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of("Maria Souza", "João Souza"), nomes(pagina));
        assertEquals(2, pagina.getPagina());
        assertEquals(2, pagina.getTotalPaginas());
    }

    @Test
    void paginaAlemDoUltimoElementoRetornaConteudoVazio() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setTamanho(2);
        dto.setPagina(50);

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertTrue(pagina.getConteudo().isEmpty());
        assertEquals(6L, pagina.getTotal());
    }

    @Test
    void paginaMuitoAltaNaoEstouraOsIndicesDaLista() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setPagina(Integer.MAX_VALUE);
        dto.setTamanho(100);

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertTrue(pagina.getConteudo().isEmpty());
        assertEquals(6L, pagina.getTotal());
    }

    @Test
    void totalExatoEmMultiplosDoTamanhoNaoArredondaParaCima() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setTamanho(3);

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(6L, pagina.getTotal());
        assertEquals(2, pagina.getTotalPaginas());
    }

    @Test
    void filtroExatoDeAgravoIgnoraMaiusculasEEspacos() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setAgravo("  dengue ");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(3L, 1L, 2L), ids(pagina));
        assertEquals(3L, pagina.getTotal());
    }

    @Test
    void filtroExatoDeUfIgnoraMaiusculas() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setUfNotificacao("pb");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(5L, 1L, 4L, 2L), ids(pagina));
    }

    @Test
    void filtroParcialDeNomeDoPacienteIgnoraMaiusculas() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setNomePaciente("MARIA");

        assertEquals(List.of(1L, 4L), ids(service.listar(dto)));

        dto.setNomePaciente("maria sou");
        assertEquals(List.of(4L), ids(service.listar(dto)));
    }

    @Test
    void filtroParcialDeMunicipioDaNotificacao() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setMunicipioNotificacao("caja");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(1L, 4L), ids(pagina));
        assertEquals(2L, pagina.getTotal());
    }

    @Test
    void periodoEhInclusivoNasDuasExtremidades() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setDataNotificacaoDe(LocalDate.of(2026, 2, 10));
        dto.setDataNotificacaoAte(LocalDate.of(2026, 3, 12));

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(1L, 4L), ids(pagina));
    }

    @Test
    void periodoComSomenteDataInicialDescartaOsMaisAntigos() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setDataNotificacaoDe(LocalDate.of(2026, 4, 1));

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(3L, 5L, 6L), ids(pagina));
    }

    @Test
    void filtrosCombinadosSaoAplicadosEmConjunto() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setAgravo("Zika");
        dto.setUfNotificacao("PB");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(5L, 4L), ids(pagina));
    }

    @Test
    void filtroSemCorrespondenciaRetornaPaginaVaziaComTotalZero() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setAgravo("Malária");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertTrue(pagina.getConteudo().isEmpty());
        assertEquals(0L, pagina.getTotal());
        assertEquals(0, pagina.getTotalPaginas());
    }

    @Test
    void ordenacaoAscendentePorNomeDoPaciente() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setOrdenarPor("nomePaciente");
        dto.setOrdem("asc");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of("Beatriz Rocha", "Carla Dias", "José Lima", "João Souza", "Maria da Silva", "Maria Souza"),
                nomes(pagina));
    }

    @Test
    void ordenacaoPorIdRespeitaAOrdemAscendente() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setOrdenarPor("id");
        dto.setOrdem("ASC");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(List.of(1L, 2L, 3L, 4L, 5L, 6L), ids(pagina));
    }

    @Test
    void filtroVazioEmBrancoEhIgnorado() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setAgravo("   ");
        dto.setNomePaciente("");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        assertEquals(6L, pagina.getTotal());
    }

    @Test
    void listagemMapeiaOsDadosDeResidencia() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setNomePaciente("Carla");

        PaginaDTO<NotificacaoResponseDTO> pagina = service.listar(dto);

        NotificacaoResponseDTO unica = pagina.getConteudo().get(0);
        assertEquals("Carla Dias", unica.getNomePaciente());
        assertEquals(LocalDate.of(2026, 6, 20), unica.getDataNotificacao());
        assertEquals("PE", unica.getResidencia().getUf());
        assertEquals("Recife", unica.getResidencia().getMunicipio());
    }
}