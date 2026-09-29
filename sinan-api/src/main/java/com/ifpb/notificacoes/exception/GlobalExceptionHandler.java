package com.ifpb.notificacoes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

public class GlobalExceptionHandler {

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
}
