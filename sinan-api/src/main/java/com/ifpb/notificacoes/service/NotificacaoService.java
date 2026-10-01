package com.ifpb.notificacoes.service;

import com.ifpb.notificacoes.dto.NotificacaoRequestDTO;
import com.ifpb.notificacoes.dto.NotificacaoResponseDTO;
import com.ifpb.notificacoes.dto.ResidenciaDTO;
import com.ifpb.notificacoes.exception.NotificacaoNaoEncontradaException;
import com.ifpb.notificacoes.model.Notificacao;
import com.ifpb.notificacoes.model.Residencia;
import com.ifpb.notificacoes.repository.NotificacaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

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
}