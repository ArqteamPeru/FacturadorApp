package com.facturador.modelo;

public class Configuracion {
    private String modo;
    private String ruc;
    private String usuarioSunat;
    private String claveSunat;
    private String certificadoPath;
    private String claveCertificado;
    private String razonSocial;
    private String direccion;
    private String correo;
    private String web;
    private String logoPath;

    // Constructor vacío (requerido para FXML o uso general)
    public Configuracion() {
    }

    // Constructor completo (útil para leer desde la BD)
    public Configuracion(String modo, String ruc, String usuarioSunat, String claveSunat,
                         String certificadoPath, String claveCertificado, String razonSocial,
                         String direccion, String correo, String web, String logoPath) {
        this.modo = modo;
        this.ruc = ruc;
        this.usuarioSunat = usuarioSunat;
        this.claveSunat = claveSunat;
        this.certificadoPath = certificadoPath;
        this.claveCertificado = claveCertificado;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.correo = correo;
        this.web = web;
        this.logoPath = logoPath;
    }

    // Getters y Setters
    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getUsuarioSunat() { return usuarioSunat; }
    public void setUsuarioSunat(String usuarioSunat) { this.usuarioSunat = usuarioSunat; }

    public String getClaveSunat() { return claveSunat; }
    public void setClaveSunat(String claveSunat) { this.claveSunat = claveSunat; }

    public String getCertificadoPath() { return certificadoPath; }
    public void setCertificadoPath(String certificadoPath) { this.certificadoPath = certificadoPath; }

    public String getClaveCertificado() { return claveCertificado; }
    public void setClaveCertificado(String claveCertificado) { this.claveCertificado = claveCertificado; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getWeb() { return web; }
    public void setWeb(String web) { this.web = web; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    @Override
    public String toString() {
        return "Configuracion{" +
                "modo='" + modo + '\'' +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                '}';
    }
}
