package com.controlhouse.utopiasoft.controlhouse.Entidades;

public class CCategorias {
    private int id;
    private String nombre;
    private int idpadre;
    private Boolean tipo;
    private boolean fija;
    private double monto;

    public int getIdpadre() {
        return idpadre;
    }

    public void setIdpadre(int idpadre) {
        this.idpadre = idpadre;
    }

    public Boolean getTipo() {
        return tipo;
    }

    public void setTipo(Boolean tipo) {
        this.tipo = tipo;
    }

    public boolean isFija() {
        return fija;
    }

    public void setFija(boolean fija) {
        this.fija = fija;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public CCategorias(){

    }

    public CCategorias(int id, String nombre, int idpadre, Boolean tipo, boolean fija, double monto) {
        this.id = id;
        this.nombre = nombre;
        this.idpadre = idpadre;
        this.tipo = tipo;
        this.fija = fija;
        this.monto = monto;
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


}
