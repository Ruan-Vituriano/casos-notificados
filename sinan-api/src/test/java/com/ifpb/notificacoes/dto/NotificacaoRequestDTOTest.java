package com.ifpb.notificacoes.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoRequestDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private NotificacaoRequestDTO dtoValido() {
        NotificacaoRequestDTO dto = new NotificacaoRequestDTO();
        dto.setAgravo("Dengue");
        dto.setDataNotificacao(LocalDate.now());
        dto.setUfNotificacao("PB");
        dto.setUnidadeSaude("UBS Centro");
        dto.setDataPrimeirosSintomas(LocalDate.now().minusDays(2));
        dto.setNomePaciente("Maria da Silva");
        dto.setDataNascimento(LocalDate.of(1990, 5, 10));
        dto.setSexo("M");

        ResidenciaDTO residencia = new ResidenciaDTO();
        residencia.setUf("PB");
        residencia.setMunicipio("Cajazeiras");
        dto.setResidencia(residencia);
        return dto;
    }

    private boolean temErro(Set<ConstraintViolation<NotificacaoRequestDTO>> violacoes, String trecho) {
        return violacoes.stream().anyMatch(v -> v.getMessage().contains(trecho));
    }

    @Test
    void notificacaoValidaNaoGeraErros() {
        assertTrue(validator.validate(dtoValido()).isEmpty());
    }

    @Test
    void semNascimentoEsemIdadeGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.setDataNascimento(null);
        assertTrue(temErro(validator.validate(dto), "idade é obrigatória"));
    }

    @Test
    void semNascimentoComIdadeEUnidadeNaoGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.setDataNascimento(null);
        dto.setIdade(26);
        dto.setUnidadeIdade(4);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void sexoFemininoAdultaSemGestanteGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.setSexo("F");
        assertTrue(temErro(validator.validate(dto), "gestante"));
    }

    @Test
    void sexoFemininoCriancaSemGestanteNaoGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.setSexo("F");
        dto.setDataNascimento(LocalDate.now().minusYears(2));
        assertFalse(temErro(validator.validate(dto), "gestante"));
    }

    @Test
    void residenciaSemUfESemPaisGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.getResidencia().setUf(null);
        assertTrue(temErro(validator.validate(dto), "país de residência"));
    }

    @Test
    void residenciaNoExteriorComPaisNaoGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.getResidencia().setUf(null);
        dto.getResidencia().setMunicipio(null);
        dto.getResidencia().setPais("Portugal");
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void ufSemMunicipioGeraErro() {
        NotificacaoRequestDTO dto = dtoValido();
        dto.getResidencia().setMunicipio(null);
        assertTrue(temErro(validator.validate(dto), "município de residência"));
    }
}