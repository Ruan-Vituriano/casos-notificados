package com.ifpb.notificacoes.model;

import java.time.LocalDate;

public class Notificacao {

    private Long id;

    // Dados gerais
    private String agravo;
    private String codigoCid10;
    private LocalDate dataNotificacao;

    private String ufNotificacao;
    private String municipioNotificacao;
    private String codigoIbgeNotificacao;

    private String unidadeSaude;
    private String codigoUnidadeSaude;
    private LocalDate dataPrimeirosSintomas;

    // Notificação individual
    private String nomePaciente;
    private LocalDate dataNascimento;

    private Integer idade;
    private Integer unidadeIdade;
    private String sexo;
    private Integer gestante;
    private Integer racaCor;

    private Integer escolaridade;

    private String cartaoSus;
    private String nomeMae;

    // Dados de residência
    private Residencia residencia;

    // Conclusão
    private LocalDate dataInvestigacao;
    private Integer classificacaoFinal;
    private Integer criterioConfirmacao;

    private Integer casoAutoctone;
    private String ufInfeccao;
    private String paisInfeccao;

    private String municipioInfeccao;
    private String codigoIbgeMunicipioInfeccao;

    private String distritoInfeccao;
    private String bairroInfeccao;

    private Integer doencaTrabalho;
    private Integer evolucaoCaso;

    private LocalDate dataObito;
    private LocalDate dataEncerramento;

    private String observacoes;

    public Notificacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgravo() {
        return agravo;
    }

    public void setAgravo(String agravo) {
        this.agravo = agravo;
    }

    public String getCodigoCid10() {
        return codigoCid10;
    }

    public void setCodigoCid10(String codigoCid10) {
        this.codigoCid10 = codigoCid10;
    }

    public LocalDate getDataNotificacao() {
        return dataNotificacao;
    }

    public void setDataNotificacao(LocalDate dataNotificacao) {
        this.dataNotificacao = dataNotificacao;
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

    public String getCodigoIbgeNotificacao() {
        return codigoIbgeNotificacao;
    }

    public void setCodigoIbgeNotificacao(String codigoIbgeNotificacao) {
        this.codigoIbgeNotificacao = codigoIbgeNotificacao;
    }

    public String getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(String unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public String getCodigoUnidadeSaude() {
        return codigoUnidadeSaude;
    }

    public void setCodigoUnidadeSaude(String codigoUnidadeSaude) {
        this.codigoUnidadeSaude = codigoUnidadeSaude;
    }

    public LocalDate getDataPrimeirosSintomas() {
        return dataPrimeirosSintomas;
    }

    public void setDataPrimeirosSintomas(LocalDate dataPrimeirosSintomas) {
        this.dataPrimeirosSintomas = dataPrimeirosSintomas;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Integer getUnidadeIdade() {
        return unidadeIdade;
    }

    public void setUnidadeIdade(Integer unidadeIdade) {
        this.unidadeIdade = unidadeIdade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Integer getGestante() {
        return gestante;
    }

    public void setGestante(Integer gestante) {
        this.gestante = gestante;
    }

    public Integer getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(Integer racaCor) {
        this.racaCor = racaCor;
    }

    public Integer getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Integer escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getCartaoSus() {
        return cartaoSus;
    }

    public void setCartaoSus(String cartaoSus) {
        this.cartaoSus = cartaoSus;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public Residencia getResidencia() {
        return residencia;
    }

    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }

    public LocalDate getDataInvestigacao() {
        return dataInvestigacao;
    }

    public void setDataInvestigacao(LocalDate dataInvestigacao) {
        this.dataInvestigacao = dataInvestigacao;
    }

    public Integer getClassificacaoFinal() {
        return classificacaoFinal;
    }

    public void setClassificacaoFinal(Integer classificacaoFinal) {
        this.classificacaoFinal = classificacaoFinal;
    }

    public Integer getCriterioConfirmacao() {
        return criterioConfirmacao;
    }

    public void setCriterioConfirmacao(Integer criterioConfirmacao) {
        this.criterioConfirmacao = criterioConfirmacao;
    }

    public Integer getCasoAutoctone() {
        return casoAutoctone;
    }

    public void setCasoAutoctone(Integer casoAutoctone) {
        this.casoAutoctone = casoAutoctone;
    }

    public String getUfInfeccao() {
        return ufInfeccao;
    }

    public void setUfInfeccao(String ufInfeccao) {
        this.ufInfeccao = ufInfeccao;
    }

    public String getPaisInfeccao() {
        return paisInfeccao;
    }

    public void setPaisInfeccao(String paisInfeccao) {
        this.paisInfeccao = paisInfeccao;
    }

    public String getMunicipioInfeccao() {
        return municipioInfeccao;
    }

    public void setMunicipioInfeccao(String municipioInfeccao) {
        this.municipioInfeccao = municipioInfeccao;
    }

    public String getCodigoIbgeMunicipioInfeccao() {
        return codigoIbgeMunicipioInfeccao;
    }

    public void setCodigoIbgeMunicipioInfeccao(String codigoIbgeMunicipioInfeccao) {
        this.codigoIbgeMunicipioInfeccao = codigoIbgeMunicipioInfeccao;
    }

    public String getDistritoInfeccao() {
        return distritoInfeccao;
    }

    public void setDistritoInfeccao(String distritoInfeccao) {
        this.distritoInfeccao = distritoInfeccao;
    }

    public String getBairroInfeccao() {
        return bairroInfeccao;
    }

    public void setBairroInfeccao(String bairroInfeccao) {
        this.bairroInfeccao = bairroInfeccao;
    }

    public Integer getDoencaTrabalho() {
        return doencaTrabalho;
    }

    public void setDoencaTrabalho(Integer doencaTrabalho) {
        this.doencaTrabalho = doencaTrabalho;
    }

    public Integer getEvolucaoCaso() {
        return evolucaoCaso;
    }

    public void setEvolucaoCaso(Integer evolucaoCaso) {
        this.evolucaoCaso = evolucaoCaso;
    }

    public LocalDate getDataObito() {
        return dataObito;
    }

    public void setDataObito(LocalDate dataObito) {
        this.dataObito = dataObito;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}