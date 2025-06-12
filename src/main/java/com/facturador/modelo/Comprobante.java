package com.facturador.modelo;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.List;

public class Comprobante {

    // Campos
    private String tipoComprobante;
    private String serie;
    private String correlativo;
    private String numeroDocumento;
    private String razonSocial;
    private String direccion;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String tipoOperacion;
    private String condicionPago;
    private String metodoPago;
    private double total;
    private List<ItemDetalle> detalles;

    // 🔽 Campos para detracción
    private boolean conDetraccion;
    private String codigoBienDetraccion;
    private Double porcentajeDetraccion;
    private Double montoDetraccion;
    private String cuentaBancoNacion;
    private String medioPagoDetraccion = "003";
    private String leyendaDetraccion = "Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central";

    // Propiedades JavaFX
    private final StringProperty tipoProperty = new SimpleStringProperty();
    private final StringProperty serieNumeroProperty = new SimpleStringProperty();
    private final StringProperty razonSocialProperty = new SimpleStringProperty();
    private final StringProperty rucDniProperty = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fechaEmisionProperty = new SimpleObjectProperty<>();
    private final DoubleProperty totalProperty = new SimpleDoubleProperty();
    private final StringProperty estadoProperty = new SimpleStringProperty("PENDIENTE");

    public Comprobante(String tipoComprobante, String serie, String correlativo, String numeroDocumento,
                       String razonSocial, String direccion, LocalDate fechaEmision, LocalDate fechaVencimiento,
                       String tipoOperacion, String condicionPago, String metodoPago, double total,
                       List<ItemDetalle> detalles) {
        this.tipoComprobante = tipoComprobante;
        this.serie = serie;
        this.correlativo = correlativo;
        this.numeroDocumento = numeroDocumento;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.tipoOperacion = tipoOperacion;
        this.condicionPago = condicionPago;
        this.metodoPago = metodoPago;
        this.total = total;
        this.detalles = detalles;

        this.tipoProperty.set(tipoComprobante);
        this.serieNumeroProperty.set(serie + "-" + correlativo);
        this.razonSocialProperty.set(razonSocial);
        this.rucDniProperty.set(numeroDocumento);
        this.fechaEmisionProperty.set(fechaEmision);
        this.totalProperty.set(total);
    }

    // JavaFX Properties
    public StringProperty tipoProperty() { return tipoProperty; }
    public StringProperty serieNumeroProperty() { return serieNumeroProperty; }
    public StringProperty clienteRazonSocialProperty() { return razonSocialProperty; }
    public StringProperty rucDniProperty() { return rucDniProperty; }
    public ObjectProperty<LocalDate> fechaEmisionProperty() { return fechaEmisionProperty; }
    public DoubleProperty totalProperty() { return totalProperty; }
    public StringProperty estadoProperty() { return estadoProperty; }

    // Getters y Setters
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
        this.tipoProperty.set(tipoComprobante);
    }

    public String getSerie() { return serie; }
    public void setSerie(String serie) {
        this.serie = serie;
        this.serieNumeroProperty.set(serie + "-" + this.correlativo);
    }

    public String getCorrelativo() { return correlativo; }
    public void setCorrelativo(String correlativo) {
        this.correlativo = correlativo;
        this.serieNumeroProperty.set(this.serie + "-" + correlativo);
    }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
        this.rucDniProperty.set(numeroDocumento);
    }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
        this.razonSocialProperty.set(razonSocial);
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
        this.fechaEmisionProperty.set(fechaEmision);
    }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public String getCondicionPago() { return condicionPago; }
    public void setCondicionPago(String condicionPago) { this.condicionPago = condicionPago; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getTotal() { return total; }
    public void setTotal(double total) {
        this.total = total;
        this.totalProperty.set(total);
    }

    public List<ItemDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<ItemDetalle> detalles) { this.detalles = detalles; }

    public String getEstado() { return estadoProperty.get(); }
    public void setEstado(String estado) { this.estadoProperty.set(estado); }

    // Getters y setters de detracción
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
}
