package com.ifpb.notificacoes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(BindException ex) {
        // Cobre @Valid no corpo da requisição (POST/PUT) e nos parâmetros de consulta do GET /notificacao
        List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::descrever)
                .toList();

        String detalhe = erros.isEmpty()
                ? "Requisição inválida"
                : erros.get(0).get("mensagem");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);

        problemDetail.setType(URI.create("https://sua-api.com/erros/requisicao-invalida"));
        problemDetail.setTitle("Requisição inválida");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        problemDetail.setProperty("erros", erros);

        return problemDetail;
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail handleRegraNegocioException(RegraNegocioException ex) {
        // Cria o ProblemDetail com o status (ex: 422 Unprocessable Entity) e o detalhe
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );

        // Define o tipo e título conforme a RFC 9457
        problemDetail.setType(URI.create("https://sua-api.com/erros/regra-de-negocio"));
        problemDetail.setTitle("Violação de Regra de Negócio");

        // Você pode adicionar propriedades customizadas que farão parte do JSON final
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        return problemDetail;
    }

    @ExceptionHandler(NotificacaoNaoEncontradaException.class)
    public ProblemDetail handleNotificacaoNaoEncontrada(NotificacaoNaoEncontradaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setType(URI.create("https://sua-api.com/erros/notificacao-nao-encontrada"));
        problemDetail.setTitle("Notificação não encontrada");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        return problemDetail;
    }

    private static Map<String, String> descrever(FieldError erro) {

        String mensagem = erro.getDefaultMessage() != null
                ? erro.getDefaultMessage()
                : "Valor inválido";

        return Map.of(
                "campo", erro.getField() != null ? erro.getField() : "",
                "mensagem", mensagem
        );
    }
}