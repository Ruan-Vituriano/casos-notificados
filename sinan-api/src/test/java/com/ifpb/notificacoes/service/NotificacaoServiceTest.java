package com.ifpb.notificacoes.service;

import com.ifpb.notificacoes.dto.NotificacaoRequestDTO;
import com.ifpb.notificacoes.dto.NotificacaoResponseDTO;
import com.ifpb.notificacoes.dto.ResidenciaDTO;
import com.ifpb.notificacoes.exception.NotificacaoNaoEncontradaException;
import com.ifpb.notificacoes.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificacaoServiceTest {

    private NotificacaoService service;

    @BeforeEach
    void preparar() {
        service = new NotificacaoService(new NotificacaoRepository());
    }

    private NotificacaoRequestDTO requisicao(String nomePaciente, String municipio) {
        NotificacaoRequestDTO dto = new NotificacaoRequestDTO();
        dto.setAgravo("Dengue");
        dto.setDataNotificacao(LocalDate.of(2026, 3, 12));
        dto.setUfNotificacao("PB");
        dto.setUnidadeSaude("UBS Centro");
        dto.setDataPrimeirosSintomas(LocalDate.of(2026, 3, 10));
        dto.setNomePaciente(nomePaciente);
        dto.setDataNascimento(LocalDate.of(1990, 5, 10));
        dto.setSexo("M");

        ResidenciaDTO residencia = new ResidenciaDTO();
        residencia.setUf("PB");
        residencia.setMunicipio(municipio);
        dto.setResidencia(residencia);
        return dto;
    }

    @Test
    void criarGeraIdECopiaTodosOsDados() {
        NotificacaoResponseDTO criada = service.criar(requisicao("Maria da Silva", "Cajazeiras"));

        assertEquals(1L, criada.getId());
        assertEquals("Dengue", criada.getAgravo());
        assertEquals("Maria da Silva", criada.getNomePaciente());
        assertEquals(LocalDate.of(1990, 5, 10), criada.getDataNascimento());
        assertEquals("Cajazeiras", criada.getResidencia().getMunicipio());
    }

    @Test
    void atualizarMantemOIdESubstituiOsDados() {
        service.criar(requisicao("Maria da Silva", "Cajazeiras"));

        NotificacaoResponseDTO atualizada = service.atualizar(1L, requisicao("Maria Souza", "Sousa"));

        assertEquals(1L, atualizada.getId());
        assertEquals("Maria Souza", atualizada.getNomePaciente());
        assertEquals("Sousa", atualizada.getResidencia().getMunicipio());
    }

    @Test
    void atualizarIdInexistenteLancaExcecao() {
        assertThrows(NotificacaoNaoEncontradaException.class,
                () -> service.atualizar(99L, requisicao("Maria", "Cajazeiras")));
    }

    @Test
    void excluirRemoveONotificacaoExistente() {
        service.criar(requisicao("Maria da Silva", "Cajazeiras"));

        service.excluir(1L);

        assertThrows(NotificacaoNaoEncontradaException.class,
                () -> service.atualizar(1L, requisicao("Maria", "Cajazeiras")));
    }

    @Test
    void excluirIdInexistenteLancaExcecao() {
        assertThrows(NotificacaoNaoEncontradaException.class, () -> service.excluir(99L));
    }
}