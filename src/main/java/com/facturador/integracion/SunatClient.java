package com.facturador.integracion;

import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.dto.RespuestaSunatDTO;
import com.facturador.util.ArchivoSunatUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.scene.control.Alert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Base64;

public class SunatClient {

    private static final String URL = "http://localhost:8080/api/facturarsunat";

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static boolean enviarComprobante(ComprobanteDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            RespuestaSunatDTO respuesta = mapper.readValue(response.body(), RespuestaSunatDTO.class);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Respuesta de SUNAT");

            if (respuesta.isExito()) {
                alert.setHeaderText("Comprobante ACEPTADO");
                alert.setContentText(respuesta.getDescripcionSunat());
                alert.showAndWait();

                // ✅ Guardar archivos
                String nombreBase = dto.getSerie() + "-" + String.format("%08d", dto.getNumero());
                LocalDate fecha = dto.getFechaEmision();
                String carpeta = "sunat/envios/" + fecha;

                if (respuesta.getZipFirmadoBase64() != null) {
                    byte[] zipFirmado = Base64.getDecoder().decode(respuesta.getZipFirmadoBase64());
                    ArchivoSunatUtil.guardarArchivoEnRuta(zipFirmado, carpeta, nombreBase + ".zip");
                }

                if (respuesta.getZipCDRBase64() != null) {
                    byte[] zipCDR = Base64.getDecoder().decode(respuesta.getZipCDRBase64());
                    ArchivoSunatUtil.guardarArchivoEnRuta(zipCDR, carpeta, "R-" + nombreBase + ".zip");
                }

                return true;

            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Error al enviar comprobante");
                alert.setContentText(respuesta.getMensaje());
                alert.showAndWait();
                return false;
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de conexión");
            alert.setHeaderText("No se pudo enviar el comprobante");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
}
