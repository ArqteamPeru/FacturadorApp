package com.facturador.ui;

import com.facturador.database.DatabaseConnection;
import com.facturador.database.Producto;
import com.facturador.util.ProductoSeleccionadoListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SeleccionarProductoController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, String> colUnidad;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colAfectacion;
    @FXML private TextField buscarField;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ProductoSeleccionadoListener listener;

    public void setListener(ProductoSeleccionadoListener listener) {
        this.listener = listener;
    }

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(data -> data.getValue().codigoProperty());
        colDescripcion.setCellValueFactory(data -> data.getValue().descripcionProperty());
        colUnidad.setCellValueFactory(data -> data.getValue().unidadProperty());
        colPrecio.setCellValueFactory(data -> data.getValue().precioUnitProperty().asObject());
        colAfectacion.setCellValueFactory(data -> data.getValue().tipoAfectacionProperty());

        buscarField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos(newVal));

        tablaProductos.setRowFactory(tv -> {
            TableRow<Producto> fila = new TableRow<>();
            fila.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !fila.isEmpty()) {
                    seleccionarProducto(fila.getItem());
                }
            });
            return fila;
        });

        cargarProductos();
    }

    private void cargarProductos() {
        listaProductos.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM producto")) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getString("unidad"),
                        rs.getDouble("precio_unit"),
                        rs.getString("tipo_afectacion")
                );
                listaProductos.add(p);
            }

            tablaProductos.setItems(listaProductos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarProductos(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tablaProductos.setItems(listaProductos);
            return;
        }

        ObservableList<Producto> filtrados = FXCollections.observableArrayList();
        for (Producto p : listaProductos) {
            if (p.getDescripcion().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(p);
            }
        }
        tablaProductos.setItems(filtrados);
    }

    @FXML
    private void agregarSeleccionado() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            seleccionarProducto(seleccionado);
        } else {
            mostrarAlerta("Seleccione un producto para agregar.");
        }
    }

    private void seleccionarProducto(Producto producto) {
        if (listener != null) {
            listener.onProductoSeleccionado(producto);
        }
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) tablaProductos.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
