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
    void semParametrosRetornaOPaginaoPadraoEmOrdemDecrescenteDeData() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(notificacao(3L, "Carla Dias")), 0, 20, 1));

        mockMvc.perform(get("/notificacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo[0].id").value(3))
                .andExpect(jsonPath("$.conteudo[0].nomePaciente").value("Carla Dias"))
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(20))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.totalPaginas").value(1));
    }

    @Test
    void parametrosDeConsultaSaoConvertidosNoFiltro() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(), 1, 5, 0));

        mockMvc.perform(get("/notificacao")
                        .param("agravo", "Dengue")
                        .param("ufNotificacao", "pb")
                        .param("municipioNotificacao", "Cajazeiras")
                        .param("nomePaciente", "maria")
                        .param("dataNotificacaoDe", "2026-01-01")
                        .param("dataNotificacaoAte", "2026-12-31")
                        .param("pagina", "1")
                        .param("tamanho", "5")
                        .param("ordenarPor", "nomePaciente")
                        .param("direcao", "asc"))
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
        assertEquals(1, recebido.getPagina());
        assertEquals(5, recebido.getTamanho());
        assertEquals("nomePaciente", recebido.getOrdenarPor());
        assertEquals("asc", recebido.getDirecao());
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
        quandoRetornar(new PaginaDTO<>(List.of(), 0, 20, 0));

        mockMvc.perform(get("/notificacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.totalPaginas").value(0));
    }

    @Test
    void respostaEhServidaComoJson() throws Exception {
        quandoRetornar(new PaginaDTO<>(List.of(notificacao(1L, "Maria")), 0, 20, 1));

        mockMvc.perform(get("/notificacao").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}