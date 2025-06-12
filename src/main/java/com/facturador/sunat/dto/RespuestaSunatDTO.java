package com.facturador.sunat.dto;

public class RespuestaSunatDTO {

    private boolean exito;
    private String mensaje;
    private String nombreArchivoXml;
    private String xmlBase64;
    private String hash;
    private String ticket;
    private String codRespuesta;
    private String descripcionSunat;

    private String cdrXml; // XML del CDR (respuesta SUNAT)

    // ✅ Nuevos campos requeridos para guardar ZIPs
    private String zipFirmadoBase64; // ZIP del XML firmado
    private String zipCDRBase64;     // ZIP del CDR recibido

    // Getters y Setters
    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreArchivoXml() {
        return nombreArchivoXml;
    }

    public void setNombreArchivoXml(String nombreArchivoXml) {
        this.nombreArchivoXml = nombreArchivoXml;
    }

    public String getXmlBase64() {
        return xmlBase64;
    }

    public void setXmlBase64(String xmlBase64) {
        this.xmlBase64 = xmlBase64;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getCodRespuesta() {
        return codRespuesta;
    }

    public void setCodRespuesta(String codRespuesta) {
        this.codRespuesta = codRespuesta;
    }

    public String getDescripcionSunat() {
        return descripcionSunat;
    }

    public void setDescripcionSunat(String descripcionSunat) {
        this.descripcionSunat = descripcionSunat;
    }

    public String getCdrXml() {
        return cdrXml;
    }

    public void setCdrXml(String cdrXml) {
        this.cdrXml = cdrXml;
    }

    // ✅ Getters y setters para los ZIPs
    public String getZipFirmadoBase64() {
        return zipFirmadoBase64;
    }

    public void setZipFirmadoBase64(String zipFirmadoBase64) {
        this.zipFirmadoBase64 = zipFirmadoBase64;
    }

    public String getZipCDRBase64() {
        return zipCDRBase64;
    }

    public void setZipCDRBase64(String zipCDRBase64) {
        this.zipCDRBase64 = zipCDRBase64;
    }
}
