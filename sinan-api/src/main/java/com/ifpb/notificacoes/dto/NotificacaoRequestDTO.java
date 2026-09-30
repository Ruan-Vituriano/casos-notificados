package com.ifpb.notificacoes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class NotificacaoRequestDTO {

    // Dados gerais
    @NotBlank(message = "O agravo/doença é obrigatório")
    private String agravo;

    private String codigoCid10;

    @NotNull(message = "A data da notificação é obrigatória")
    @PastOrPresent(message = "A data da notificação não pode ser futura")
    private LocalDate dataNotificacao;

    private String ufNotificacao;
    private String municipioNotificacao;
    private String codigoIbgeNotificacao;

    private String unidadeSaude;
    private String codigoUnidadeSaude;

    @PastOrPresent(message = "A data dos primeiros sintomas não pode ser futura")
    private LocalDate dataPrimeirosSintomas;

    // Notificação individual
    @NotBlank(message = "O nome do paciente é obrigatório")
    private String nomePaciente;

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    @PositiveOrZero(message = "A idade não pode ser negativa")
    private Integer idade;

    private Integer unidadeIdade;

    @Pattern(regexp = "[MFImfi]", message = "Sexo deve ser M, F ou I")
    private String sexo;

    private Integer gestante;
    private Integer racaCor;

    private Integer escolaridade;

    @Pattern(regexp = "\\d{15}", message = "O cartão SUS deve ter 15 dígitos")
    private String cartaoSus;

    @NotBlank(message = "O nome da mãe é obrigatório")
    private String nomeMae;

    // Dados de residência
    @Valid
    @NotNull(message = "Os dados de residência são obrigatórios")
    private ResidenciaDTO residencia;

    // Conclusão
    @PastOrPresent(message = "A data da investigação não pode ser futura")
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

    @PastOrPresent(message = "A data do óbito não pode ser futura")
    private LocalDate dataObito;

    @PastOrPresent(message = "A data de encerramento não pode ser futura")
    private LocalDate dataEncerramento;

    private String observacoes;

    // Dados do investigador
    private String municipioUnidadeInvestigador;
    private String codigoUnidadeInvestigador;
    private String nomeInvestigador;
    private String funcaoInvestigador;

    public NotificacaoRequestDTO() {
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

    public ResidenciaDTO getResidencia() {
        return residencia;
    }

    public void setResidencia(ResidenciaDTO residencia) {
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

    public String getMunicipioUnidadeInvestigador() {
        return municipioUnidadeInvestigador;
    }

    public void setMunicipioUnidadeInvestigador(String municipioUnidadeInvestigador) {
        this.municipioUnidadeInvestigador = municipioUnidadeInvestigador;
    }

    public String getCodigoUnidadeInvestigador() {
        return codigoUnidadeInvestigador;
    }

    public void setCodigoUnidadeInvestigador(String codigoUnidadeInvestigador) {
        this.codigoUnidadeInvestigador = codigoUnidadeInvestigador;
    }

    public String getNomeInvestigador() {
        return nomeInvestigador;
    }

    public void setNomeInvestigador(String nomeInvestigador) {
        this.nomeInvestigador = nomeInvestigador;
    }

    public String getFuncaoInvestigador() {
        return funcaoInvestigador;
    }

    public void setFuncaoInvestigador(String funcaoInvestigador) {
        this.funcaoInvestigador = funcaoInvestigador;
    }
}