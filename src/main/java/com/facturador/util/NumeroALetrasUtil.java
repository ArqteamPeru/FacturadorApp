package com.facturador.util;

import java.text.DecimalFormat;

public class NumeroALetrasUtil {

    private static final String[] UNIDADES = {
            "", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE",
            "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISÉIS", "DIECISIETE",
            "DIECIOCHO", "DIECINUEVE", "VEINTE"
    };

    private static final String[] DECENAS = {
            "", "", "VEINTI", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA",
            "OCHENTA", "NOVENTA"
    };

    private static final String[] CENTENAS = {
            "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS",
            "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
    };

    public static String convertir(double numero) {
        if (numero == 0) {
            return "SON: CERO CON 00/100 SOLES";
        }

        long parteEntera = (long) numero;
        int parteDecimal = (int) Math.round((numero - parteEntera) * 100);

        return "SON: " + convertirNumero(parteEntera).trim() + " CON "
                + (parteDecimal < 10 ? "0" + parteDecimal : parteDecimal) + "/100 SOLES";
    }

    private static String convertirNumero(long numero) {
        if (numero == 0) {
            return "CERO";
        } else if (numero < 21) {
            return UNIDADES[(int) numero];
        } else if (numero < 100) {
            int decena = (int) (numero / 10);
            int unidad = (int) (numero % 10);
            if (numero <= 29) {
                return DECENAS[decena] + UNIDADES[unidad].toLowerCase();
            } else {
                return DECENAS[decena] + (unidad > 0 ? " Y " + UNIDADES[unidad] : "");
            }
        } else if (numero < 1000) {
            int centena = (int) (numero / 100);
            int resto = (int) (numero % 100);
            if (numero == 100) {
                return "CIEN";
            }
            return CENTENAS[centena] + " " + convertirNumero(resto);
        } else if (numero < 1_000_000) {
            long miles = numero / 1000;
            int resto = (int) (numero % 1000);
            String milesTexto = miles == 1 ? "MIL" : convertirNumero(miles) + " MIL";
            return milesTexto + (resto > 0 ? " " + convertirNumero(resto) : "");
        } else if (numero < 1_000_000_000) {
            long millones = numero / 1_000_000;
            long resto = numero % 1_000_000;
            String millonesTexto = millones == 1 ? "UN MILLÓN" : convertirNumero(millones) + " MILLONES";
            return millonesTexto + (resto > 0 ? " " + convertirNumero(resto) : "");
        } else {
            return "Número demasiado grande";
        }
    }
}
