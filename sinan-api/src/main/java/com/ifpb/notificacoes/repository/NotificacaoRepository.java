package com.ifpb.notificacoes.repository;

import com.ifpb.notificacoes.model.Notificacao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class NotificacaoRepository {

    private final List<Notificacao> notificacoes = new ArrayList<>();

    private Long proximoId = 1L;

    public synchronized Notificacao salvar(Notificacao notificacao) {

        if (notificacao.getId() == null) {
            notificacao.setId(proximoId);
            proximoId++;
            notificacoes.add(notificacao);
            return notificacao;
        }

        for (int i = 0; i < notificacoes.size(); i++) {
            if (notificacoes.get(i).getId().equals(notificacao.getId())) {
                notificacoes.set(i, notificacao);
                return notificacao;
            }
        }

        notificacoes.add(notificacao);
        proximoId = Math.max(proximoId, notificacao.getId() + 1);

        return notificacao;
    }

    public synchronized Optional<Notificacao> buscarPorId(Long id) {

        return notificacoes.stream()
                .filter(notificacao -> notificacao.getId().equals(id))
                .findFirst();
    }

    public synchronized List<Notificacao> listarTodos() {

        return new ArrayList<>(notificacoes);
    }

    public synchronized boolean excluirPorId(Long id) {

        return notificacoes.removeIf(notificacao -> notificacao.getId().equals(id));
    }
}