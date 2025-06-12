package com.facturador.sunat.util;

public enum SunatRespuestaTipo {
    ACEPTADO("0", "Aceptado"),
    OBSERVADO("98", "Observado"),
    RECHAZADO("99", "Rechazado"),
    DESCONOCIDO("XX", "Desconocido");

    private final String codigo;
    private final String descripcion;

    SunatRespuestaTipo(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static String interpretar(String codigo) {
        for (SunatRespuestaTipo tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo.descripcion;
            }
        }
        return DESCONOCIDO.descripcion;
    }
}
