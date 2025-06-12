package com.facturador.ui;

import com.facturador.database.Cliente;
import com.facturador.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component // 👈 NECESARIO para que Spring lo registre
public class ClienteListController {

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TableColumn<Cliente, String> colTipoDoc;

    @FXML
    private TableColumn<Cliente, String> colNumeroDoc;

    @FXML
    private TableColumn<Cliente, String> colRazonSocial;

    @FXML
    private TableColumn<Cliente, String> colDireccion;

    @FXML
    private TextField busquedaField;

    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTipoDoc.setCellValueFactory(new PropertyValueFactory<>("tipoDoc"));
        colNumeroDoc.setCellValueFactory(new PropertyValueFactory<>("numeroDoc"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        cargarClientes();

        busquedaField.textProperty().addListener((obs, oldVal, newVal) -> filtrarClientes(newVal));
    }

    private void cargarClientes() {
        listaClientes.clear();
        String sql = "SELECT * FROM cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("tipo_doc"),
                        rs.getString("numero_doc"),
                        rs.getString("razon_social"),
                        rs.getString("direccion")
                );
                listaClientes.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tablaClientes.setItems(listaClientes);
    }

    private void filtrarClientes(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tablaClientes.setItems(listaClientes);
            return;
        }

        ObservableList<Cliente> filtrados = FXCollections.observableArrayList();
        for (Cliente c : listaClientes) {
            if (c.getNumeroDoc().toLowerCase().contains(filtro.toLowerCase()) ||
                    c.getRazonSocial().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(c);
            }
        }

        tablaClientes.setItems(filtrados);
    }

    @FXML
    private void irAFormulario() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/cliente_form.fxml"));
            Stage stage = (Stage) tablaClientes.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarSeleccionado() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este cliente?");
            confirmacion.setContentText("Cliente: " + seleccionado.getRazonSocial());

            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    String sql = "DELETE FROM cliente WHERE numero_doc = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, seleccionado.getNumeroDoc());
                        stmt.executeUpdate();
                        System.out.println("✅ Cliente eliminado.");
                        recargarTabla();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            mostrarAlerta("Debes seleccionar un cliente para eliminar.");
        }
    }

    @FXML
    private void recargarTabla() {
        cargarClientes();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
