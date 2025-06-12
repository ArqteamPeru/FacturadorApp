package com.facturador.sunat.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuotaDTO {
    private BigDecimal importe;
    private LocalDate fechaPago;

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
}
