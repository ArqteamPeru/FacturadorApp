package com.facturador.util;

public class ComboItem {
    private String codigo;
    private String descripcion;

    public ComboItem(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion; // será sobreescrito dinámicamente en el ComboBox
    }
}
