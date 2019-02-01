package com.controlhouse.utopiasoft.controlhouse.Entidades;

import java.util.Date;

public class CTransaciones {
    private int id;
    private int cuentaIdOrigen;
    private int cuentaIdDestino;
    private String cuentaOrigen;
    private String cuentaDestino;
    private Date fecha;
    private double monto;
    private String descripcion;

    public CTransaciones() {
    }

    public CTransaciones(int id, int cuentaIdOrigen, int cuentaIdDestino, String cuentaOrigen, String cuentaDestino, Date fecha, double monto, String descripcion) {
        this.id = id;
        this.cuentaIdOrigen = cuentaIdOrigen;
        this.cuentaIdDestino = cuentaIdDestino;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.fecha = fecha;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCuentaIdOrigen() {
        return cuentaIdOrigen;
    }

    public void setCuentaIdOrigen(int cuentaIdOrigen) {
        this.cuentaIdOrigen = cuentaIdOrigen;
    }

    public int getCuentaIdDestino() {
        return cuentaIdDestino;
    }

    public void setCuentaIdDestino(int cuentaIdDestino) {
        this.cuentaIdDestino = cuentaIdDestino;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
}
