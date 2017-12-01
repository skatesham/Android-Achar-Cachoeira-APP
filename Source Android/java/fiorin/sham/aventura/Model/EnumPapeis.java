/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fiorin.sham.aventura.Model;

/**
 *
 * @author sham
 */
public enum EnumPapeis {
    ADMINISTRADOR ("Administrador"),
    USUARIO_COMUM ("Usuário Comum"),
    VISITANTE ("Visitante");
    private final String descricao;

    private EnumPapeis(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
        
}

