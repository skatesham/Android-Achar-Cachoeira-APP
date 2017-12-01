/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fiorin.sham.aventura.Model;

import java.util.List;

/**
 *
 * @author sham
 */
public class Usuario extends Identificador{
    
    private String nome;
    private String email;
    private String senha;
    private String nomeUsuario;
    private List<Papel> papeis;

    public Usuario() {
    }

     
    public Usuario(int id) {
        super(id);
    }

    public Usuario(String nome, String email, String senha, String nomeUsuario, List<Papel> papeis, int id) {
        super(id);
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.nomeUsuario = nomeUsuario;
        this.papeis = papeis;
    }

    public String getNome() {
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public List<Papel> getPapeis() {
        return papeis;
    }

    public void setPapeis(List<Papel> papeis) {
        this.papeis = papeis;
    }
       
}

