package com.ifpb.notificacoes.controller;

import com.ifpb.notificacoes.dto.NotificacaoFiltroDTO;
import com.ifpb.notificacoes.dto.NotificacaoResponseDTO;
import com.ifpb.notificacoes.dto.PaginaDTO;
import com.ifpb.notificacoes.service.NotificacaoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificacaoController.class)
class NotificacaoControllerListagemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacaoService service;

    private NotificacaoResponseDTO notificacao(Long id, String nome) {
        NotificacaoResponseDTO dto = new NotificacaoResponseDTO();
        dto.setId(id);
        dto.setNomePaciente(nome);
        dto.setAgravo("Dengue");
        return dto;
    }

    private void quandoRetornar(PaginaDTO<NotificacaoResponseDTO> pagina) {
        when(service.listar(any(NotificacaoFiltroDTO.class))).thenReturn(pagina);
    }

    @Test
    void semParametrosUsaPaginaUmTamanhoDezEOrdemDecrescentePorDataDeNotificacao() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(), 1, 10, 0));

        mockMvc.perform(get("/notificacao")).andExpect(status().isOk());

        ArgumentCaptor<NotificacaoFiltroDTO> filtro = ArgumentCaptor.forClass(NotificacaoFiltroDTO.class);
        verify(service).listar(filtro.capture());

        NotificacaoFiltroDTO recebido = filtro.getValue();
        assertEquals(1, recebido.getPagina());
        assertEquals(10, recebido.getTamanho());
        assertEquals("dataNotificacao", recebido.getOrdenarPor());
        assertEquals("DESC", recebido.getOrdem());
        assertFalse(recebido.isBuscarDuplicadas());
    }

    @Test
    void semParametrosRetornaAPrimeiraPaginaComDezItensEmOrdemDecrescenteDeData() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(notificacao(3L, "Carla Dias")), 1, 10, 1));

        mockMvc.perform(get("/notificacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo[0].id").value(3))
                .andExpect(jsonPath("$.conteudo[0].nomePaciente").value("Carla Dias"))
                .andExpect(jsonPath("$.pagina").value(1))
                .andExpect(jsonPath("$.tamanho").value(10))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.totalPaginas").value(1));
    }

    @Test
    void parametrosDeConsultaSaoConvertidosNoFiltro() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(), 2, 5, 0));

        mockMvc.perform(get("/notificacao")
                        .param("agravo", "Dengue")
                        .param("ufNotificacao", "pb")
                        .param("municipioNotificacao", "Cajazeiras")
                        .param("nomePaciente", "maria")
                        .param("dataNotificacaoDe", "2026-01-01")
                        .param("dataNotificacaoAte", "2026-12-31")
                        .param("duplicadas", "true")
                        .param("pagina", "2")
                        .param("tamanho", "5")
                        .param("ordenarPor", "nomePaciente")
                        .param("ordem", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isEmpty());

        ArgumentCaptor<NotificacaoFiltroDTO> filtro = ArgumentCaptor.forClass(NotificacaoFiltroDTO.class);
        verify(service).listar(filtro.capture());

        NotificacaoFiltroDTO recebido = filtro.getValue();
        assertEquals("Dengue", recebido.getAgravo());
        assertEquals("pb", recebido.getUfNotificacao());
        assertEquals("Cajazeiras", recebido.getMunicipioNotificacao());
        assertEquals("maria", recebido.getNomePaciente());
        assertEquals(LocalDate.of(2026, 1, 1), recebido.getDataNotificacaoDe());
        assertEquals(LocalDate.of(2026, 12, 31), recebido.getDataNotificacaoAte());
        assertTrue(recebido.isBuscarDuplicadas());
        assertEquals(2, recebido.getPagina());
        assertEquals(5, recebido.getTamanho());
        assertEquals("nomePaciente", recebido.getOrdenarPor());
        assertEquals("ASC", recebido.getOrdem());
    }

    @Test
    void tamanhoDePaginaInvalidoRetornaProblemaDeRequisicaoInvalida() throws Exception {
        mockMvc.perform(get("/notificacao").param("tamanho", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição inválida"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erros[0].campo").value("tamanho"));
    }

    @Test
    void tamanhoDePaginaAcimaDoMaximoRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("tamanho", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("tamanho"));
    }

    @Test
    void tamanhoNaoNumericoRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("tamanho", "dez"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("tamanho"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("Valor inválido para o parâmetro 'tamanho'"));
    }

    @Test
    void paginaZeroRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("pagina", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pagina"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("A página deve ser no mínimo 1"));
    }

    @Test
    void paginaNegativaRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("pagina", "-3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pagina"));
    }

    @Test
    void paginaNaoNumericaRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("pagina", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("pagina"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("Valor inválido para o parâmetro 'pagina'"));
    }

    @Test
    void ordemDesconhecidaRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("ordem", "CIMA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("ordem"))
                .andExpect(jsonPath("$.erros[0].mensagem").value("A ordem deve ser ASC ou DESC"));
    }

    @Test
    void ordemVaziaRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("ordem", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("ordem"));
    }

    @Test
    void ordemEmMinusculasEhAceita() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(), 1, 10, 0));

        mockMvc.perform(get("/notificacao").param("ordem", "asc"))
                .andExpect(status().isOk());
    }

    @Test
    void duplicadasNaoBooleanoRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/notificacao").param("duplicadas", "talvez"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("duplicadas"));
    }

    @Test
    void campoDeOrdenacaoDesconhecidoRetornaProblemaDeRequisicaoInvalida() throws Exception {
        mockMvc.perform(get("/notificacao").param("ordenarPor", "nomeMae"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("ordenarPor"));
    }

    @Test
    void dataInvalidaNoPeriodoRetornaProblemaDeRequisicaoInvalida() throws Exception {
        mockMvc.perform(get("/notificacao").param("dataNotificacaoDe", "12/03/2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("dataNotificacaoDe"));
    }

    @Test
    void periodoInvertidoRetornaProblemaDeRequisicaoInvalida() throws Exception {
        mockMvc.perform(get("/notificacao")
                        .param("dataNotificacaoDe", "2026-12-31")
                        .param("dataNotificacaoAte", "2026-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].mensagem").value(
                        "A data inicial do período deve ser anterior ou igual à data final"));
    }

    @Test
    void listagemSemRegistrosRetornaPaginaVaziaComTotalZero() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(), 1, 10, 0));

        mockMvc.perform(get("/notificacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.totalPaginas").value(0));
    }

    @Test
    void respostaEhServidaComoJson() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(notificacao(1L, "Maria")), 1, 10, 1));

        mockMvc.perform(get("/notificacao").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}