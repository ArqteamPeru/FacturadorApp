package com.facturador.ui;

import com.facturador.config.ApplicationContextProvider;
import com.facturador.database.DatabaseConnection;
import com.facturador.database.Producto;
import com.facturador.database.ProductoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component  // ✅ Para que Spring lo reconozca como bean
public class ProductoListController {

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colDescripcion;

    @FXML
    private TableColumn<Producto, String> colUnidad;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, String> colAfectacion;

    @FXML
    private TextField buscarField;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(data -> data.getValue().codigoProperty());
        colDescripcion.setCellValueFactory(data -> data.getValue().descripcionProperty());
        colUnidad.setCellValueFactory(data -> data.getValue().unidadProperty());
        colPrecio.setCellValueFactory(data -> data.getValue().precioUnitProperty().asObject());
        colAfectacion.setCellValueFactory(data -> data.getValue().tipoAfectacionProperty());

        buscarField.textProperty().addListener((obs, oldVal, newVal) -> filtrarProductos(newVal));

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
    private void eliminarSeleccionado() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Debe seleccionar un producto o servicio.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setContentText("¿Estás seguro que deseas eliminar el producto: " + seleccionado.getDescripcion() + "?");

        confirmacion.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                ProductoDAO.eliminarProducto(seleccionado.getCodigo());
                cargarProductos();
            }
        });
    }

    @FXML
    private void editarSeleccionado() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/producto_form.fxml"));
            loader.setControllerFactory(ApplicationContextProvider.getContext()::getBean); // 🔄 Spring factory
            Parent root = loader.load();

            ProductoFormController controller = loader.getController();
            controller.setProductoEditar(seleccionado);

            Stage stage = (Stage) tablaProductos.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAFormulario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/producto_form.fxml"));
            loader.setControllerFactory(ApplicationContextProvider.getContext()::getBean); // 🔄 Spring factory
            Parent root = loader.load();

            Stage stage = (Stage) tablaProductos.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void recargarTabla() {
        cargarProductos();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
