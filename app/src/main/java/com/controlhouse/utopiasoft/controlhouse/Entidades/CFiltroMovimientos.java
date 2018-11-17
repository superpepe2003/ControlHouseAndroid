package com.controlhouse.utopiasoft.controlhouse.Entidades;

import java.util.Date;

public class CFiltroMovimientos {
    int Fecha; // 1 ORDENA ASCENDENTE Y 2 ORDENA DESCENDENTE
    boolean filtroPorFecha;
    int tipo; // -1 EGRESOS / 0 TODOS / 1 INGRESOS
    String fechaInicial;
    String fechaFinal;
    Double montoMinimo;
    Double montoMaximo;
    String contenido;

    public CFiltroMovimientos(int fecha, boolean filtroPorFecha, String fechaInicial, String fechaFinal, Double montoMinimo, Double montoMaximo, String contenido, int tipo) {
        Fecha = fecha;
        this.filtroPorFecha = filtroPorFecha;
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
        this.contenido=contenido;
        this.tipo=tipo;
    }

    public CFiltroMovimientos(){

    }



    public boolean isFiltroPorFecha() {
        return filtroPorFecha;
    }

    public void setFiltroPorFecha(boolean filtroPorFecha) {
        this.filtroPorFecha = filtroPorFecha;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public int getFecha() {
        return Fecha;
    }

    public void setFecha(int fecha) {
        Fecha = fecha;
    }

    public Double getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(Double montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public Double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(Double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
