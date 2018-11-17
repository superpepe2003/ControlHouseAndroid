package com.controlhouse.utopiasoft.controlhouse.Entidades;

public class CUsuario {
    String usuario;
    String password;

    public CUsuario(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public CUsuario() {

    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
