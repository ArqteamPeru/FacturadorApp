package com.facturador.database;

import javafx.beans.property.*;

public class Producto {

    private final StringProperty codigo = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final StringProperty unidad = new SimpleStringProperty();
    private final DoubleProperty precioUnit = new SimpleDoubleProperty();
    private final StringProperty tipoAfectacion = new SimpleStringProperty();

    public Producto(String codigo, String descripcion, String unidad, double precioUnit, String tipoAfectacion) {
        this.codigo.set(codigo);
        this.descripcion.set(descripcion);
        this.unidad.set(unidad);
        this.precioUnit.set(precioUnit);
        this.tipoAfectacion.set(tipoAfectacion);
    }

    public String getCodigo() {
        return codigo.get();
    }

    public void setCodigo(String value) {
        codigo.set(value);
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String value) {
        descripcion.set(value);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public String getUnidad() {
        return unidad.get();
    }

    public void setUnidad(String value) {
        unidad.set(value);
    }

    public StringProperty unidadProperty() {
        return unidad;
    }

    public double getPrecioUnit() {
        return precioUnit.get();
    }

    public void setPrecioUnit(double value) {
        precioUnit.set(value);
    }

    public DoubleProperty precioUnitProperty() {
        return precioUnit;
    }

    public String getTipoAfectacion() {
        return tipoAfectacion.get();
    }

    public void setTipoAfectacion(String value) {
        tipoAfectacion.set(value);
    }

    public StringProperty tipoAfectacionProperty() {
        return tipoAfectacion;
    }

    @Override
    public String toString() {
        return getDescripcion() + " (" + getCodigo() + ")";
    }
}
