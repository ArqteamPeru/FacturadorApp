package com.facturador.sunat.util;

import io.github.project.openubl.xsender.files.ZipFile;

public class ZipXmlSunatUtil {

    /**
     * Genera un archivo ZIP válido para SUNAT usando la clase ZipFile de XSender.
     *
     * @param nombreArchivoXml Nombre del XML (ej. 20123456789-01-F001-12345.xml)
     * @param xmlFirmadoBytes  Contenido del XML firmado
     * @return Instancia de ZipFile lista para enviar
     */
    public static ZipFile generarZipSunat(String nombreArchivoXml, byte[] xmlFirmadoBytes) {
        return new ZipFile(xmlFirmadoBytes, nombreArchivoXml);
    }
}
