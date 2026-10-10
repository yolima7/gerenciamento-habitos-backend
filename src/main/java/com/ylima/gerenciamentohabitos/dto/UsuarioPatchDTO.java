package com.ylima.gerenciamentohabitos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class UsuarioPatchDTO {

    @Pattern(regexp = "^(?=.*\\p{L})[\\p{L} ]+$")
    private String nome;


    @Email
    private String email;

    public  String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
