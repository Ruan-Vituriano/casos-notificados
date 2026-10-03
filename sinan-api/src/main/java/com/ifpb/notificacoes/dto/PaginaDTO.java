package com.ifpb.notificacoes.dto;

import java.util.List;

/**
 * Envelope de paginação usado nas respostas de listagem.
 *
 * @param <T> tipo dos itens da página
 */
public class PaginaDTO<T> {

    private List<T> conteudo;
    private int pagina;
    private int tamanho;
    private long total;
    private int totalPaginas;

    public PaginaDTO() {
    }

    public PaginaDTO(List<T> conteudo, int pagina, int tamanho, long total) {

        this.conteudo = conteudo;
        this.pagina = pagina;
        this.tamanho = tamanho;
        this.total = total;
        this.totalPaginas = tamanho <= 0 ? 0 : (int) Math.ceil(total / (double) tamanho);
    }

    public List<T> getConteudo() {
        return conteudo;
    }

    public void setConteudo(List<T> conteudo) {
        this.conteudo = conteudo;
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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}