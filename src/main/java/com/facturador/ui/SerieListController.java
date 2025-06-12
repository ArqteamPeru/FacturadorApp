package com.facturador.ui;

import com.facturador.database.DatabaseConnection;
import com.facturador.database.SerieDAO;
import com.facturador.modelo.Serie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class SerieListController {

    @FXML private TableView<Serie> tablaSeries;
    @FXML private TableColumn<Serie, String> colTipo;
    @FXML private TableColumn<Serie, String> colSerie;
    @FXML private TableColumn<Serie, Integer> colCorrelativo;

    private ObservableList<Serie> listaSeries;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colTipo.setCellValueFactory(cellData -> cellData.getValue().tipoComprobanteProperty());
        colSerie.setCellValueFactory(cellData -> cellData.getValue().serieProperty());
        colCorrelativo.setCellValueFactory(cellData -> cellData.getValue().correlativoProperty().asObject());

        colSerie.setCellFactory(TextFieldTableCell.forTableColumn());
        colSerie.setOnEditCommit(event -> {
            Serie s = event.getRowValue();
            s.setSerie(event.getNewValue().trim());
        });

        colCorrelativo.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCorrelativo.setOnEditCommit(event -> {
            Serie s = event.getRowValue();
            s.setCorrelativo(event.getNewValue());
        });

        tablaSeries.setEditable(true);
    }

    @FXML
    private void cargarDatos() {
        listaSeries = FXCollections.observableArrayList(SerieDAO.obtenerTodas());
        tablaSeries.setItems(listaSeries);
    }

    @FXML
    private void guardarCambios() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (Serie s : listaSeries) {
                SerieDAO.actualizar(s, conn);
            }

            conn.commit();

            mostrarMensaje(Alert.AlertType.INFORMATION, "✅ Cambios guardados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(Alert.AlertType.ERROR, "❌ Error al guardar los cambios:\n" + e.getMessage());
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Mensaje del sistema");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
