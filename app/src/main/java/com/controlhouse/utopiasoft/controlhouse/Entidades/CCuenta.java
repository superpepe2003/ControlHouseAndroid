package com.controlhouse.utopiasoft.controlhouse.Entidades;

public class CCuenta {
    private int id;
    private String nombre;
    private float saldo;

    public CCuenta(){

    }

    public CCuenta(int id, String nombre, float saldo) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
}
