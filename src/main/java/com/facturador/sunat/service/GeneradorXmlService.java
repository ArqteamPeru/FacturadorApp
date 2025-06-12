package com.facturador.sunat.service;

import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.dto.RespuestaSunatDTO;
import com.facturador.sunat.mapper.MapperDtoToInvoice;

import io.github.project.openubl.xbuilder.content.models.standard.general.Invoice;
import io.github.project.openubl.xbuilder.enricher.ContentEnricher;
import io.github.project.openubl.xbuilder.enricher.config.DateProvider;
import io.github.project.openubl.xbuilder.enricher.config.Defaults;
import io.github.project.openubl.xbuilder.renderer.TemplateProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;

@Service
public class GeneradorXmlService {

    private final MapperDtoToInvoice mapperDtoToInvoice;

    @Autowired
    public GeneradorXmlService(MapperDtoToInvoice mapperDtoToInvoice) {
        this.mapperDtoToInvoice = mapperDtoToInvoice;
    }

    public RespuestaSunatDTO generarXml(ComprobanteDTO dto) {
        RespuestaSunatDTO respuesta = new RespuestaSunatDTO();

        try {
            // 1. Mapear DTO → Invoice
            Invoice invoice = mapperDtoToInvoice.map(dto);

            // 2. Configurar Defaults
            Defaults defaults = Defaults.builder()
                    .igvTasa(new BigDecimal("0.18"))
                    .icbTasa(BigDecimal.ZERO)
                    .build();

            // 3. Fecha de emisión
            DateProvider dateProvider = () -> LocalDate.now();

            // 4. Enriquecer datos
            ContentEnricher enricher = new ContentEnricher(defaults, dateProvider);
            enricher.enrich(invoice);

            // 5. Generar XML con plantilla
            String xml = TemplateProducer.getInstance().getInvoice().data(invoice).render();

            // 6. Construir nombre archivo: RUC-TIPO-SERIE-NUMERO.xml
            String nombreArchivo = invoice.getProveedor().getRuc() + "-" +
                    invoice.getTipoComprobante() + "-" +
                    invoice.getSerie() + "-" +
                    invoice.getNumero() + ".xml";

            // 7. Preparar respuesta
            respuesta.setExito(true);
            respuesta.setMensaje("XML generado correctamente");
            respuesta.setNombreArchivoXml(nombreArchivo);
            respuesta.setXmlBase64(Base64.getEncoder().encodeToString(xml.getBytes()));

            return respuesta;

        } catch (Exception e) {
            respuesta.setExito(false);
            respuesta.setMensaje("Error al generar XML: " + e.getMessage());
            return respuesta;
        }
    }
}
