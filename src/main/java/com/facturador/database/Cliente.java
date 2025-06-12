package com.facturador.database;

public class Cliente {
    private String tipoDoc;
    private String numeroDoc;
    private String razonSocial;
    private String direccion;

    public Cliente(String tipoDoc, String numeroDoc, String razonSocial, String direccion) {
        this.tipoDoc = tipoDoc;
        this.numeroDoc = numeroDoc;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public String getNumeroDoc() {
        return numeroDoc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }
}
