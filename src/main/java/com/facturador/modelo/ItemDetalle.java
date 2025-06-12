package com.facturador.modelo;

import javafx.beans.property.*;

public class ItemDetalle {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final StringProperty unidad = new SimpleStringProperty();
    private final DoubleProperty cantidad = new SimpleDoubleProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final StringProperty tipoAfectacion = new SimpleStringProperty("10"); // Por defecto: gravado IGV

    public ItemDetalle(String codigo, String descripcion, String unidad, double cantidad, double precio) {
        this.codigo.set(codigo);
        this.descripcion.set(descripcion);
        this.unidad.set(unidad);
        this.cantidad.set(cantidad);
        this.precio.set(precio);
        recalcularTotal();
    }

    public void recalcularTotal() {
        total.set(cantidad.get() * precio.get());
    }

    // Getters
    public String getCodigo() { return codigo.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public String getUnidad() { return unidad.get(); }
    public double getCantidad() { return cantidad.get(); }
    public double getPrecio() { return precio.get(); }
    public double getTotal() { return total.get(); }
    public String getTipoAfectacion() { return tipoAfectacion.get(); }

    public double getSubtotal() {
        return getTotal(); // Aquí podrías calcular sin IGV si deseas desglosar
    }

    public double getPrecioUnitario() {
        return getPrecio();  // Alias
    }

    // Setters
    public void setCantidad(double cantidad) {
        this.cantidad.set(cantidad);
        recalcularTotal();
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
        recalcularTotal();
    }

    public void setTipoAfectacion(String tipo) {
        this.tipoAfectacion.set(tipo);
    }

    // Properties
    public StringProperty descripcionProperty() { return descripcion; }
    public DoubleProperty cantidadProperty() { return cantidad; }
    public DoubleProperty precioProperty() { return precio; }
    public DoubleProperty totalProperty() { return total; }
    public StringProperty unidadProperty() { return unidad; }
    public StringProperty codigoProperty() { return codigo; }
    public StringProperty tipoAfectacionProperty() { return tipoAfectacion; }
}
