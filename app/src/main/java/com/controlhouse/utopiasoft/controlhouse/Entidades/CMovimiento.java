package com.controlhouse.utopiasoft.controlhouse.Entidades;

import android.support.annotation.NonNull;

import java.util.Date;

public class CMovimiento {
    private int id;
    private Date fecha;
    private String categoria;
    private String subCategoria;
    private String cuenta;
    private double monto;
    private String descripcion;
    private String hashtag;
    private Boolean Tipo;

    private int idCategoria;
    private int idSubCategoria;
    private int idCuenta;

    public CMovimiento() {
    }

    public CMovimiento(int id, Date fecha, String categoria, String subCategoria, String cuenta, double monto, String descripcion, String hashtag, Boolean tipo, int idCategoria, int idSubCategoria, int idCuenta) {
        this.id = id;
        this.fecha = fecha;
        this.categoria = categoria;
        this.subCategoria = subCategoria;
        this.cuenta = cuenta;
        this.monto = monto;
        this.descripcion = descripcion;
        this.hashtag = hashtag;
        Tipo = tipo;
        this.idCategoria = idCategoria;
        this.idSubCategoria = idSubCategoria;
        this.idCuenta = idCuenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdSubCategoria() {
        return idSubCategoria;
    }

    public void setIdSubCategoria(int idSubCategoria) {
        this.idSubCategoria = idSubCategoria;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubCategoria() {
        return subCategoria;
    }

    public void setSubCategoria(String subCategoria) {
        this.subCategoria = subCategoria;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Boolean getTipo() {
        return Tipo;
    }

    public void setTipo(Boolean tipo) {
        Tipo = tipo;
    }


}
