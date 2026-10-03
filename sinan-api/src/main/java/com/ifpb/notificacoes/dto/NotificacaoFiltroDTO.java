package com.ifpb.notificacoes.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Parâmetros de consulta do GET /notificacao: filtros, paginação e ordenação.
 *
 * <p>Todos os parâmetros são opcionais. Quando ausentes, valem os valores padrão
 * dos campos, o que equivale a "sem filtro" e "primeira página, 20 itens,
 * data de notificação mais recente primeiro".
 */
public class NotificacaoFiltroDTO {

    public static final String ORDENAR_POR_PADRAO = "dataNotificacao";
    public static final String DIRECAO_PADRAO = "desc";
    public static final int TAMANHO_PADRAO = 20;

    // Filtros

    private String agravo;
    private String ufNotificacao;
    private String municipioNotificacao;
    private String nomePaciente;
    private LocalDate dataNotificacaoDe;
    private LocalDate dataNotificacaoAte;

    // Paginação

    @Min(value = 0, message = "A página não pode ser negativa")
    private int pagina = 0;

    @Min(value = 1, message = "O tamanho da página deve ser no mínimo 1")
    @Max(value = 100, message = "O tamanho da página deve ser no máximo 100")
    private int tamanho = TAMANHO_PADRAO;

    // Ordenação

    @NotBlank(message = "Informe o campo de ordenação")
    @Pattern(
            regexp = "^(id|agravo|nomePaciente|municipioNotificacao|dataNotificacao)$",
            message = "Só é possível ordenar por: id, agravo, nomePaciente, municipioNotificacao ou dataNotificacao"
    )
    private String ordenarPor = ORDENAR_POR_PADRAO;

    @Pattern(
            regexp = "(?i)^(asc|desc)$",
            message = "A direção da ordenação deve ser asc ou desc"
    )
    private String direcao = DIRECAO_PADRAO;

    public NotificacaoFiltroDTO() {
    }

    public String getAgravo() {
        return agravo;
    }

    public void setAgravo(String agravo) {
        this.agravo = agravo;
    }

    public String getUfNotificacao() {
        return ufNotificacao;
    }

    public void setUfNotificacao(String ufNotificacao) {
        this.ufNotificacao = ufNotificacao;
    }

    public String getMunicipioNotificacao() {
        return municipioNotificacao;
    }

    public void setMunicipioNotificacao(String municipioNotificacao) {
        this.municipioNotificacao = municipioNotificacao;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public LocalDate getDataNotificacaoDe() {
        return dataNotificacaoDe;
    }

    public void setDataNotificacaoDe(LocalDate dataNotificacaoDe) {
        this.dataNotificacaoDe = dataNotificacaoDe;
    }

    public LocalDate getDataNotificacaoAte() {
        return dataNotificacaoAte;
    }

    public void setDataNotificacaoAte(LocalDate dataNotificacaoAte) {
        this.dataNotificacaoAte = dataNotificacaoAte;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public String getOrdenarPor() {
        return ordenarPor;
    }

    public void setOrdenarPor(String ordenarPor) {
        this.ordenarPor = ordenarPor;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    // Validação condicional do período

    @AssertTrue(message = "A data inicial do período deve ser anterior ou igual à data final")
    public boolean isPeriodoValido() {
        return dataNotificacaoDe == null || dataNotificacaoAte == null
                || !dataNotificacaoDe.isAfter(dataNotificacaoAte);
    }
}