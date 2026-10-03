package com.ifpb.notificacoes.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacaoFiltroDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private NotificacaoFiltroDTO filtro() {
        return new NotificacaoFiltroDTO();
    }

    private boolean temErro(Set<ConstraintViolation<NotificacaoFiltroDTO>> violacoes, String trecho) {
        return violacoes.stream().anyMatch(v -> v.getMessage().contains(trecho));
    }

    @Test
    void filtroSemParametrosUsaOsPadroesDaTabela() {
        NotificacaoFiltroDTO dto = filtro();

        assertTrue(validator.validate(dto).isEmpty());
        assertEquals(0, dto.getPagina());
        assertEquals(20, dto.getTamanho());
        assertEquals("dataNotificacao", dto.getOrdenarPor());
        assertEquals("desc", dto.getDirecao());
    }

    @Test
    void filtrosPreenchidosECorretosNaoGeramErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setAgravo("Dengue");
        dto.setUfNotificacao("PB");
        dto.setMunicipioNotificacao("Cajazeiras");
        dto.setNomePaciente("Maria");
        dto.setDataNotificacaoDe(LocalDate.of(2026, 1, 1));
        dto.setDataNotificacaoAte(LocalDate.of(2026, 12, 31));
        dto.setPagina(2);
        dto.setTamanho(50);
        dto.setOrdenarPor("nomePaciente");
        dto.setDirecao("asc");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void paginaNegativaGeraErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setPagina(-1);

        assertTrue(temErro(validator.validate(dto), "página não pode ser negativa"));
    }

    @Test
    void tamanhoForaDosLimitesGeraErro() {
        NotificacaoFiltroDTO zero = filtro();
        zero.setTamanho(0);
        assertTrue(temErro(validator.validate(zero), "no mínimo 1"));

        NotificacaoFiltroDTO grande = filtro();
        grande.setTamanho(101);
        assertTrue(temErro(validator.validate(grande), "no máximo 100"));
    }

    @Test
    void campoDeOrdenacaoDesconhecidoGeraErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setOrdenarPor("nomeMae");

        assertTrue(temErro(validator.validate(dto), "nomePaciente"));
    }

    @Test
    void direcaoDesconhecidaGeraErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setDirecao("lateral");

        assertTrue(temErro(validator.validate(dto), "asc ou desc"));
    }

    @Test
    void direcaoEmMaiusculasNaoGeraErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setDirecao("DESC");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void periodoInvertidoGeraErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setDataNotificacaoDe(LocalDate.of(2026, 12, 31));
        dto.setDataNotificacaoAte(LocalDate.of(2026, 1, 1));

        assertTrue(temErro(validator.validate(dto), "data inicial do período"));
    }

    @Test
    void periodoComAsMesmasDatasNaoGeraErro() {
        NotificacaoFiltroDTO dto = filtro();
        dto.setDataNotificacaoDe(LocalDate.of(2026, 3, 12));
        dto.setDataNotificacaoAte(LocalDate.of(2026, 3, 12));

        assertTrue(validator.validate(dto).isEmpty());
    }
}