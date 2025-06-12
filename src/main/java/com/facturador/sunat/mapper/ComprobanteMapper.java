// ComprobanteMapper.java
package com.facturador.sunat.mapper;

import com.facturador.modelo.Comprobante;
import com.facturador.modelo.ItemDetalle;
import com.facturador.modelo.Configuracion;
import com.facturador.database.ConfiguracionDAO;
import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.dto.ItemDetalleDTO;
import java.util.List;
import java.util.stream.Collectors;

public class ComprobanteMapper {
    public static ComprobanteDTO toDTO(Comprobante c) {
        ComprobanteDTO dto = new ComprobanteDTO();
        dto.setTipoComprobante(c.getTipoComprobante());
        dto.setSerie(c.getSerie());
        dto.setNumero(Integer.parseInt(c.getCorrelativo()));
        dto.setFechaEmision(c.getFechaEmision());

        Configuracion config = new ConfiguracionDAO().obtener();
        if (config != null) {
            dto.setRucEmisor(config.getRuc());
            dto.setRazonSocialEmisor(config.getRazonSocial());
        }
        dto.setTipoDocumentoCliente(c.getNumeroDocumento().length() == 11 ? "6" : "1");
        dto.setNumeroDocumentoCliente(c.getNumeroDocumento());
        dto.setRazonSocialCliente(c.getRazonSocial());
        dto.setDireccionCliente(c.getDireccion());
        dto.setConDetraccion(c.isConDetraccion());
        if (c.isConDetraccion()) {
            dto.setCodigoBienDetraccion(c.getCodigoBienDetraccion());
            dto.setPorcentajeDetraccion(c.getPorcentajeDetraccion());
            dto.setMontoDetraccion(c.getMontoDetraccion());
            dto.setCuentaBancoNacion(c.getCuentaBancoNacion());
            dto.setMedioPagoDetraccion(c.getMedioPagoDetraccion());
            dto.setLeyendaDetraccion(c.getLeyendaDetraccion());
        }
        List<ItemDetalleDTO> items = c.getDetalles().stream().map(item -> {
            ItemDetalleDTO i = new ItemDetalleDTO();
            i.setDescripcion(item.getDescripcion());
            i.setCantidad(item.getCantidad());
            i.setPrecioUnit(item.getPrecioUnitario());
            i.setUnidad(item.getUnidad());
            i.setTipoAfectacion(item.getTipoAfectacion());
            i.setSubtotal(item.getSubtotal());
            return i;
        }).collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }
}