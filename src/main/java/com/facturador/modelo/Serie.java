package com.facturador.modelo;

import javafx.beans.property.*;

public class Serie {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty codigoTipo = new SimpleStringProperty();           // ← agregado
    private StringProperty tipoComprobante = new SimpleStringProperty();
    private StringProperty serie = new SimpleStringProperty();
    private IntegerProperty correlativo = new SimpleIntegerProperty();

    public Serie(int id, String codigoTipo, String tipoComprobante, String serie, int correlativo) {
        this.id.set(id);
        this.codigoTipo.set(codigoTipo);                                      // ← asignado
        this.tipoComprobante.set(tipoComprobante);
        this.serie.set(serie);
        this.correlativo.set(correlativo);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getCodigoTipo() { return codigoTipo.get(); }               // ← getter
    public void setCodigoTipo(String codigoTipo) { this.codigoTipo.set(codigoTipo); } // ← setter
    public StringProperty codigoTipoProperty() { return codigoTipo; }

    public String getTipoComprobante() { return tipoComprobante.get(); }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante.set(tipoComprobante); }
    public StringProperty tipoComprobanteProperty() { return tipoComprobante; }

    public String getSerie() { return serie.get(); }
    public void setSerie(String serie) { this.serie.set(serie); }
    public StringProperty serieProperty() { return serie; }

    public int getCorrelativo() { return correlativo.get(); }
    public void setCorrelativo(int correlativo) { this.correlativo.set(correlativo); }
    public IntegerProperty correlativoProperty() { return correlativo; }
}
