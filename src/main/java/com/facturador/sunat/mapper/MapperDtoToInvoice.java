package com.facturador.sunat.mapper;

import com.facturador.modelo.Configuracion;
import com.facturador.rules.KieSessionFactory;
import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.dto.CuotaDTO;
import com.facturador.sunat.dto.ItemDetalleDTO;
import com.facturador.sunat.util.ConfiguracionSunatUtil;

import io.github.project.openubl.xbuilder.content.models.common.Cliente;
import io.github.project.openubl.xbuilder.content.models.common.Direccion;
import io.github.project.openubl.xbuilder.content.models.common.Proveedor;
import io.github.project.openubl.xbuilder.content.models.standard.general.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MapperDtoToInvoice {

    private final ConfiguracionSunatUtil configuracionSunatUtil;

    @Autowired
    public MapperDtoToInvoice(ConfiguracionSunatUtil configuracionSunatUtil) {
        this.configuracionSunatUtil = configuracionSunatUtil;
    }

    public Invoice map(ComprobanteDTO dto) {
        Configuracion config = configuracionSunatUtil.obtenerConfiguracionActiva();

        Proveedor proveedor = Proveedor.builder()
                .ruc(config.getRuc())
                .razonSocial(config.getRazonSocial())
                .direccion(Direccion.builder()
                        .direccion(config.getDireccion())
                        .ubigeo("150101")
                        .departamento("LIMA")
                        .provincia("LIMA")
                        .distrito("LIMA")
                        .codigoPais("PE")
                        .build())
                .build();

        Cliente cliente = Cliente.builder()
                .nombre(dto.getRazonSocialCliente())
                .numeroDocumentoIdentidad(dto.getNumeroDocumentoCliente())
                .tipoDocumentoIdentidad(dto.getTipoDocumentoCliente())
                .direccion(Direccion.builder()
                        .direccion(dto.getDireccionCliente() != null ? dto.getDireccionCliente() : "SIN DIRECCIÓN")
                        .build())
                .build();

        Invoice.InvoiceBuilder builder = Invoice.builder()
                .serie(dto.getSerie())
                .numero(dto.getNumero())
                .tipoComprobante(dto.getTipoComprobante())
                .proveedor(proveedor)
                .cliente(cliente)
                .fechaVencimiento(dto.getFechaEmision().plusDays(30)); // puede adaptarse a dto.getFechaVencimiento()

        // Detalles
        for (ItemDetalleDTO item : dto.getItems()) {
            BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());
            BigDecimal precioUnit = BigDecimal.valueOf(item.getPrecioUnit());
            BigDecimal subtotal = BigDecimal.valueOf(item.getSubtotal());
            BigDecimal tasaIgv = new BigDecimal("0.18");
            BigDecimal igv = subtotal.multiply(tasaIgv);

            DocumentoVentaDetalle detalle = DocumentoVentaDetalle.builder()
                    .descripcion(item.getDescripcion())
                    .unidadMedida(item.getUnidad())
                    .cantidad(cantidad)
                    .precio(precioUnit)
                    .igvTipo(item.getTipoAfectacion())
                    .tasaIgv(tasaIgv)
                    .igv(igv)
                    .igvBaseImponible(subtotal)
                    .build();

            builder.detalle(detalle);
        }

        // Forma de pago
        if ("Crédito".equalsIgnoreCase(dto.getCondicionPago()) && dto.getCuotas() != null && !dto.getCuotas().isEmpty()) {
            FormaDePago.FormaDePagoBuilder formaCredito = FormaDePago.builder().tipo("Credito");
            for (CuotaDTO cuota : dto.getCuotas()) {
                if (cuota.getImporte() != null && cuota.getFechaPago() != null) {
                    formaCredito.cuota(
                            CuotaDePago.builder()
                                    .importe(cuota.getImporte())
                                    .fechaPago(cuota.getFechaPago())
                                    .build()
                    );
                }
            }
            builder.formaDePago(formaCredito.build());
        } else {
            builder.formaDePago(FormaDePago.builder().tipo("Contado").build());
        }

        // Detracción
        if (dto.isConDetraccion()
                && dto.getCodigoBienDetraccion() != null && !dto.getCodigoBienDetraccion().isBlank()
                && dto.getPorcentajeDetraccion() != null && dto.getPorcentajeDetraccion() > 0
                && dto.getMedioPagoDetraccion() != null && !dto.getMedioPagoDetraccion().isBlank()
                && dto.getCuentaBancoNacion() != null && !dto.getCuentaBancoNacion().isBlank()) {

            Detraccion.DetraccionBuilder detraccion = Detraccion.builder()
                    .tipoBienDetraido(dto.getCodigoBienDetraccion())
                    .porcentaje(BigDecimal.valueOf(dto.getPorcentajeDetraccion()))
                    .medioDePago(dto.getMedioPagoDetraccion())
                    .cuentaBancaria(dto.getCuentaBancoNacion());

            if (dto.getMontoDetraccion() != null) {
                detraccion.monto(BigDecimal.valueOf(dto.getMontoDetraccion()));
            }

            builder.detraccion(detraccion.build());

            if (dto.getLeyendaDetraccion() != null && !dto.getLeyendaDetraccion().isBlank()) {
                builder.leyenda("2000", dto.getLeyendaDetraccion());
            }
        }

        // Reglas de enriquecimiento y resumen
        Invoice invoice = builder.build();
        KieSessionFactory.applyEnrichRules(invoice);
        KieSessionFactory.applySummaryRules(invoice);
        return invoice;
    }
}
