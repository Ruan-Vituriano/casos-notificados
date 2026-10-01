package com.ifpb.notificacoes.exception;

public class NotificacaoNaoEncontradaException extends RuntimeException {

    public NotificacaoNaoEncontradaException(Long id) {
        super("Notificação com id " + id + " não encontrada");
    }
}