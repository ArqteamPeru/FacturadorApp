package com.facturador.ui;

import com.facturador.database.Cliente;
import com.facturador.database.ClienteDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClienteFormController {

    @FXML
    private ComboBox<String> tipoDocCombo;

    @FXML
    private TextField numeroDocField;

    @FXML
    private TextField razonSocialField;

    @FXML
    private TextField direccionField;

    @FXML
    public void initialize() {
        tipoDocCombo.getItems().addAll(
                "1 - DNI",
                "4 - Carnet de Extranjería",
                "6 - RUC",
                "7 - Pasaporte",
                "0 - Otro"
        );
    }

    @FXML
    private void registrarCliente() {
        try {
            String tipoDoc = tipoDocCombo.getValue().split(" - ")[0];
            String numeroDoc = numeroDocField.getText();
            String razonSocial = razonSocialField.getText();
            String direccion = direccionField.getText();

            Cliente cliente = new Cliente(tipoDoc, numeroDoc, razonSocial, direccion);
            ClienteDAO.insertarCliente(cliente);

            System.out.println("✅ Cliente guardado.");
            limpiarCampos();

        } catch (Exception e) {
            System.out.println("⚠️ Error al registrar cliente: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        tipoDocCombo.getSelectionModel().clearSelection();
        numeroDocField.clear();
        razonSocialField.clear();
        direccionField.clear();
    }

    @FXML
    private void irAListado() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/cliente_list.fxml"));
            Stage stage = (Stage) tipoDocCombo.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
