package com.facturador.sunat.controller;

import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.dto.RespuestaSunatDTO;
import com.facturador.sunat.service.FacturacionSunatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturarsunat")
@CrossOrigin(origins = "*") // Habilita CORS si la UI está en otro dominio
public class FacturacionController {

    private final FacturacionSunatService facturacionSunatService;

    @Autowired
    public FacturacionController(FacturacionSunatService facturacionSunatService) {
        this.facturacionSunatService = facturacionSunatService;
    }

    @PostMapping
    public ResponseEntity<?> procesarComprobante(@RequestBody ComprobanteDTO dto) {
        try {
            if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("El comprobante o sus ítems están vacíos.");
            }

            RespuestaSunatDTO respuesta = facturacionSunatService.procesar(dto);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            e.printStackTrace(); // útil para desarrollo
            return ResponseEntity.internalServerError()
                    .body("❌ Error al procesar comprobante: " + e.getMessage());
        }
    }
}
