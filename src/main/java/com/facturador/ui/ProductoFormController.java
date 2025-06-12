package com.facturador.ui;

import com.facturador.config.ApplicationContextProvider;
import com.facturador.database.Producto;
import com.facturador.database.ProductoDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Consumer;

@Component // ✅ Esto permite que Spring gestione el controlador
public class ProductoFormController {

    @FXML private ComboBox<String> tipoCombo;
    @FXML private TextField descripcionField;
    @FXML private ComboBox<String> unidadCombo;
    @FXML private TextField precioField;
    @FXML private ComboBox<String> afectacionCombo;

    private Producto productoEditar;

    private Consumer<Producto> callbackProductoAgregado;

    @FXML
    public void initialize() {
        tipoCombo.getItems().addAll("PRODUCTO", "SERVICIO");

        afectacionCombo.getItems().addAll(
                "10 - Gravado - Operación Onerosa",
                "20 - Exonerado",
                "30 - Inafecto",
                "40 - Exportación"
        );

        tipoCombo.setOnAction(e -> actualizarUnidadSegunTipo());
    }

    private void actualizarUnidadSegunTipo() {
        unidadCombo.getItems().clear();

        if ("PRODUCTO".equals(tipoCombo.getValue())) {
            unidadCombo.getItems().addAll(
                    "NIU - Unidad", "KGM - Kilogramo", "LTR - Litro",
                    "MTR - Metro", "H87 - Pieza", "BTL - Botella",
                    "PA - Paquete", "BX - Caja", "BG - Bolsa", "SET - Juego/Set"
            );
        } else if ("SERVICIO".equals(tipoCombo.getValue())) {
            unidadCombo.getItems().add("ZZ - Servicio");
        }

        unidadCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void registrarProducto() {
        try {
            String tipo = tipoCombo.getValue();
            String descripcion = descripcionField.getText().toUpperCase(Locale.ROOT).trim();

            if (descripcion.isEmpty()) {
                mostrarAlerta("Descripción es obligatoria.");
                return;
            }

            String precioTexto = precioField.getText().trim();
            if (!precioTexto.matches("^\\d+(\\.\\d{1,2})?$")) {
                mostrarAlerta("Precio inválido. Use solo números positivos, máx. 2 decimales.");
                return;
            }

            double precio = Double.parseDouble(precioTexto);
            if (precio <= 0) {
                mostrarAlerta("El precio debe ser mayor que cero.");
                return;
            }

            String unidad = unidadCombo.getValue().split(" - ")[0];
            String afectacion = afectacionCombo.getValue().split(" - ")[0];

            if (productoEditar != null) {
                productoEditar.setDescripcion(descripcion);
                productoEditar.setUnidad(unidad);
                productoEditar.setPrecioUnit(precio);
                productoEditar.setTipoAfectacion(afectacion);

                ProductoDAO.actualizarProducto(productoEditar);
                mostrarInfo("✅ Producto actualizado correctamente.");
                return;
            }

            String codigoGenerado = ProductoDAO.generarCodigo(tipo);
            Producto nuevo = new Producto(codigoGenerado, descripcion, unidad, precio, afectacion);

            ProductoDAO.insertarProducto(nuevo);

            if (callbackProductoAgregado != null) {
                callbackProductoAgregado.accept(nuevo);
                cerrarVentana();
                return;
            }

            mostrarInfo("✅ " + tipo + " registrado con código: " + codigoGenerado);
            limpiarCampos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void irAListado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/producto_list.fxml"));
            loader.setControllerFactory(clase -> ApplicationContextProvider.getContext().getBean(clase));
            Parent root = loader.load();
            Stage stage = (Stage) descripcionField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProductoEditar(Producto producto) {
        this.productoEditar = producto;

        tipoCombo.setValue(producto.getCodigo().startsWith("S") ? "SERVICIO" : "PRODUCTO");
        actualizarUnidadSegunTipo();

        descripcionField.setText(producto.getDescripcion());
        unidadCombo.setValue(producto.getUnidad() + " - ");
        precioField.setText(String.format("%.2f", producto.getPrecioUnit()));
        afectacionCombo.setValue(producto.getTipoAfectacion() + " - ");
    }

    public void setCallbackProductoAgregado(Consumer<Producto> callback) {
        this.callbackProductoAgregado = callback;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) descripcionField.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        tipoCombo.getSelectionModel().clearSelection();
        descripcionField.clear();
        unidadCombo.getItems().clear();
        precioField.clear();
        afectacionCombo.getSelectionModel().clearSelection();
    }
}
