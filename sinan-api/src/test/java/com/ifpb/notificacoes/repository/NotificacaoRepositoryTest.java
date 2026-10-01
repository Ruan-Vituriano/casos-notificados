package com.ifpb.notificacoes.repository;

import com.ifpb.notificacoes.model.Notificacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoRepositoryTest {

    private Notificacao nova(String nomePaciente) {
        Notificacao notificacao = new Notificacao();
        notificacao.setAgravo("Dengue");
        notificacao.setNomePaciente(nomePaciente);
        return notificacao;
    }

    @Test
    void salvarGeraIdsSequenciais() {
        NotificacaoRepository repository = new NotificacaoRepository();

        Notificacao primeira = repository.salvar(nova("Maria"));
        Notificacao segunda = repository.salvar(nova("João"));

        assertEquals(1L, primeira.getId());
        assertEquals(2L, segunda.getId());
        assertEquals(2, repository.listarTodos().size());
    }

    @Test
    void buscarPorIdEncontraORegistroSalvo() {
        NotificacaoRepository repository = new NotificacaoRepository();
        repository.salvar(nova("Maria"));

        assertEquals("Maria", repository.buscarPorId(1L).orElseThrow().getNomePaciente());
        assertTrue(repository.buscarPorId(99L).isEmpty());
    }

    @Test
    void salvarComIdExistenteSubstituiORegistro() {
        NotificacaoRepository repository = new NotificacaoRepository();
        repository.salvar(nova("Maria"));

        Notificacao alterada = nova("Maria da Silva");
        alterada.setId(1L);
        repository.salvar(alterada);

        assertEquals(1, repository.listarTodos().size());
        assertEquals("Maria da Silva", repository.buscarPorId(1L).orElseThrow().getNomePaciente());
    }

    @Test
    void excluirPorIdRemoveERetornaFalsoQuandoNaoExiste() {
        NotificacaoRepository repository = new NotificacaoRepository();
        repository.salvar(nova("Maria"));

        assertTrue(repository.excluirPorId(1L));
        assertFalse(repository.excluirPorId(1L));
        assertTrue(repository.buscarPorId(1L).isEmpty());
    }
}