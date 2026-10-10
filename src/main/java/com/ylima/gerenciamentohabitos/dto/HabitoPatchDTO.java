package com.ylima.gerenciamentohabitos.dto;
import com.ylima.gerenciamentohabitos.entity.Periodo;
import jakarta.validation.constraints.Min;

public class HabitoPatchDTO {

    private String titulo;

    private String descricao;

    @Min(1)
    private Integer quantidade;

    private String unidade;

    private Periodo periodo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
}
