package com.controlhouse.utopiasoft.controlhouse.Entidades;

import java.text.ParseException;

public interface IFiltro {
    void setFiltro(CFiltroMovimientos filtro) throws ParseException;
    void cargarWSMovimientos();
}
