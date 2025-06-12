package com.facturador.sunat.dto;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

public class ComprobanteDTO {

    private String tipoComprobante;
    private String serie;
    private int numero;
    private LocalDate fechaEmision;

    private String rucEmisor;
    private String razonSocialEmisor;

    private String tipoDocumentoCliente;
    private String numeroDocumentoCliente;
    private String razonSocialCliente;
    private String direccionCliente;

    private boolean conDetraccion;
    private String codigoBienDetraccion;
    private Double porcentajeDetraccion;
    private Double montoDetraccion;
    private String cuentaBancoNacion;
    private String medioPagoDetraccion;
    private String leyendaDetraccion;

    private String condicionPago; // "Contado" o "Credito"
    private List<CuotaDTO> cuotas; // Si aplica crédito

    private List<ItemDetalleDTO> items;

    // Getters y Setters
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getRucEmisor() { return rucEmisor; }
    public void setRucEmisor(String rucEmisor) { this.rucEmisor = rucEmisor; }

    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }

    public String getTipoDocumentoCliente() { return tipoDocumentoCliente; }
    public void setTipoDocumentoCliente(String tipoDocumentoCliente) { this.tipoDocumentoCliente = tipoDocumentoCliente; }

    public String getNumeroDocumentoCliente() { return numeroDocumentoCliente; }
    public void setNumeroDocumentoCliente(String numeroDocumentoCliente) { this.numeroDocumentoCliente = numeroDocumentoCliente; }

    public String getRazonSocialCliente() { return razonSocialCliente; }
    public void setRazonSocialCliente(String razonSocialCliente) { this.razonSocialCliente = razonSocialCliente; }

    public String getDireccionCliente() { return direccionCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

    public boolean isConDetraccion() { return conDetraccion; }
    public void setConDetraccion(boolean conDetraccion) { this.conDetraccion = conDetraccion; }

    public String getCodigoBienDetraccion() { return codigoBienDetraccion; }
    public void setCodigoBienDetraccion(String codigoBienDetraccion) { this.codigoBienDetraccion = codigoBienDetraccion; }

    public Double getPorcentajeDetraccion() { return porcentajeDetraccion; }
    public void setPorcentajeDetraccion(Double porcentajeDetraccion) { this.porcentajeDetraccion = porcentajeDetraccion; }

    public Double getMontoDetraccion() { return montoDetraccion; }
    public void setMontoDetraccion(Double montoDetraccion) { this.montoDetraccion = montoDetraccion; }

    public String getCuentaBancoNacion() { return cuentaBancoNacion; }
    public void setCuentaBancoNacion(String cuentaBancoNacion) { this.cuentaBancoNacion = cuentaBancoNacion; }

    public String getMedioPagoDetraccion() { return medioPagoDetraccion; }
    public void setMedioPagoDetraccion(String medioPagoDetraccion) { this.medioPagoDetraccion = medioPagoDetraccion; }

    public String getLeyendaDetraccion() { return leyendaDetraccion; }
    public void setLeyendaDetraccion(String leyendaDetraccion) { this.leyendaDetraccion = leyendaDetraccion; }

    public String getCondicionPago() { return condicionPago; }
    public void setCondicionPago(String condicionPago) { this.condicionPago = condicionPago; }

    public List<CuotaDTO> getCuotas() { return cuotas; }
    public void setCuotas(List<CuotaDTO> cuotas) { this.cuotas = cuotas; }

    public List<ItemDetalleDTO> getItems() { return items; }
    public void setItems(List<ItemDetalleDTO> items) { this.items = items; }
}
