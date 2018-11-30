package com.controlhouse.utopiasoft.controlhouse.Entidades;

public class CSubCategorias {
    private int id;
    private String nombre;
    private int idCategoria;
    private String Categoria;
    private boolean fija;
    private float monto;
    private boolean tipo;

    public CSubCategorias(){}

    public CSubCategorias(int id, String nombre, int idCategoria, String categoria, boolean fija, float monto, boolean tipo) {
        this.id = id;
        this.nombre = nombre;
        this.idCategoria = idCategoria;
        Categoria = categoria;
        this.fija = fija;
        this.monto = monto;
        this.tipo = tipo;
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

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public boolean isFija() {
        return fija;
    }

    public void setFija(boolean fija) {
        this.fija = fija;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public boolean isTipo() {
        return tipo;
    }

    public void setTipo(boolean tipo) {
        this.tipo = tipo;
    }
}
