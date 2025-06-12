package com.facturador.ui;

import com.facturador.database.ComprobanteDAO;
import com.facturador.integracion.SunatClient;
import com.facturador.modelo.Comprobante;
import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.mapper.ComprobanteMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComprobanteListController {

    @FXML private TableView<Comprobante> tablaComprobantes;
    @FXML private TableColumn<Comprobante, String> colTipo;
    @FXML private TableColumn<Comprobante, String> colSerieNumero;
    @FXML private TableColumn<Comprobante, String> colCliente;
    @FXML private TableColumn<Comprobante, String> colRucDni;
    @FXML private TableColumn<Comprobante, Number> colTotal;
    @FXML private TableColumn<Comprobante, String> colFecha;
    @FXML private TableColumn<Comprobante, String> colEstado;
    @FXML private TextField filtroTexto;

    private ObservableList<Comprobante> listaComprobantes;

    @FXML
    public void initialize() {
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        colSerieNumero.setCellValueFactory(cellData -> cellData.getValue().serieNumeroProperty());
        colCliente.setCellValueFactory(cellData -> cellData.getValue().clienteRazonSocialProperty());
        colRucDni.setCellValueFactory(cellData -> cellData.getValue().rucDniProperty());
        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaEmision().toString()));
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());

        cargarDatos();
    }

    private void cargarDatos() {
        listaComprobantes = FXCollections.observableArrayList(ComprobanteDAO.obtenerTodos());
        tablaComprobantes.setItems(listaComprobantes);
    }

    @FXML
    private void onActualizarTabla() {
        cargarDatos();
    }

    @FXML
    private void onBuscar() {
        String texto = filtroTexto.getText();
        if (texto == null || texto.isEmpty()) {
            tablaComprobantes.setItems(listaComprobantes);
        } else {
            List<Comprobante> filtrados = listaComprobantes.stream()
                    .filter(c -> c.getRazonSocial().toLowerCase().contains(texto.toLowerCase()) ||
                            c.getNumeroDocumento().toLowerCase().contains(texto.toLowerCase()) ||
                            c.getEstado().toLowerCase().contains(texto.toLowerCase()))
                    .collect(Collectors.toList());

            tablaComprobantes.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

    @FXML
    private void onEnviarASunat() {
        Comprobante seleccionado = tablaComprobantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "Seleccione un comprobante para enviar.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(seleccionado.getEstado())) {
            mostrarAlerta(AlertType.INFORMATION, "Ya enviado", "Este comprobante ya fue procesado.");
            return;
        }

        ComprobanteDTO dto = ComprobanteMapper.toDTO(seleccionado);
        boolean exito = SunatClient.enviarComprobante(dto);

        if (exito) {
            seleccionado.setEstado("ACEPTADO");
            ComprobanteDAO.actualizarEstado(seleccionado);
            tablaComprobantes.refresh();
        }
    }

    @FXML
    private void onVerZipFirmado() {
        Comprobante seleccionado = tablaComprobantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "Seleccione un comprobante.");
            return;
        }

        String fechaCarpeta = seleccionado.getFechaEmision().format(DateTimeFormatter.ISO_DATE);
        String nombre = seleccionado.getSerie() + "-" + String.format("%08d", Integer.parseInt(seleccionado.getCorrelativo()));
        abrirArchivo("sunat/envios/" + fechaCarpeta + "/" + nombre + ".zip");
    }

    @FXML
    private void onVerCDR() {
        Comprobante seleccionado = tablaComprobantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", "Seleccione un comprobante.");
            return;
        }

        String fechaCarpeta = seleccionado.getFechaEmision().format(DateTimeFormatter.ISO_DATE);
        String nombre = "R-" + seleccionado.getSerie() + "-" + String.format("%08d", Integer.parseInt(seleccionado.getCorrelativo()));
        abrirArchivo("sunat/envios/" + fechaCarpeta + "/" + nombre + ".zip");
    }

    private void abrirArchivo(String ruta) {
        try {
            File file = new File(ruta);
            if (!file.exists()) {
                mostrarAlerta(AlertType.ERROR, "No encontrado", "Archivo no encontrado: " + ruta);
                return;
            }

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(file);
                } else {
                    mostrarAlerta(AlertType.WARNING, "No soportado", "La acción de abrir archivos no está soportada.");
                }
            } else {
                mostrarAlerta(AlertType.WARNING, "No disponible", "El entorno actual no soporta operaciones gráficas.");
            }

        } catch (java.awt.HeadlessException e) {
            mostrarAlerta(AlertType.ERROR, "Headless", "No se puede abrir archivo en entorno sin GUI: " + e.getMessage());
        } catch (IOException | UnsupportedOperationException e) {
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo abrir el archivo: " + e.getMessage());
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
