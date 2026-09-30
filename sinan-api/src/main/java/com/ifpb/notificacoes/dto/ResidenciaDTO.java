package com.ifpb.notificacoes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResidenciaDTO {

    @NotBlank(message = "A UF de residência é obrigatória")
    @Pattern(regexp = "[A-Za-z]{2}", message = "A UF deve ter 2 letras")
    private String uf;

    @NotBlank(message = "O município de residência é obrigatório")
    private String municipio;

    private String codigoIbge;
    private String distrito;

    private String bairro;
    private String logradouro;
    private String codigoLogradouro;

    private String numero;
    private String complemento;
    private String geoCampo1;

    private String geoCampo2;
    private String pontoReferencia;

    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido (use 00000-000 ou 00000000)")
    private String cep;

    private String telefone;
    private Integer zona;
    private String pais;

    public ResidenciaDTO() {
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCodigoIbge() {
        return codigoIbge;
    }

    public void setCodigoIbge(String codigoIbge) {
        this.codigoIbge = codigoIbge;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getCodigoLogradouro() {
        return codigoLogradouro;
    }

    public void setCodigoLogradouro(String codigoLogradouro) {
        this.codigoLogradouro = codigoLogradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getGeoCampo1() {
        return geoCampo1;
    }

    public void setGeoCampo1(String geoCampo1) {
        this.geoCampo1 = geoCampo1;
    }

    public String getGeoCampo2() {
        return geoCampo2;
    }

    public void setGeoCampo2(String geoCampo2) {
        this.geoCampo2 = geoCampo2;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getZona() {
        return zona;
    }

    public void setZona(Integer zona) {
        this.zona = zona;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}