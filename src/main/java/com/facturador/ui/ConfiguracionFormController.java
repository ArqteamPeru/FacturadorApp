package com.facturador.ui;

import com.facturador.database.ConfiguracionDAO;
import com.facturador.modelo.Configuracion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ConfiguracionFormController {

    @FXML
    private ComboBox<String> comboModo;
    @FXML
    private TextField txtRuc, txtUsuarioSunat, txtClaveSunat, txtCertificado, txtClaveCertificado,
            txtRazonSocial, txtDireccion, txtCorreo, txtWeb, txtLogo;
    @FXML
    private Button btnSeleccionarCertificado, btnSeleccionarLogo;

    private final ConfiguracionDAO dao = new ConfiguracionDAO();

    @FXML
    public void initialize() {
        comboModo.getItems().addAll("PRUEBA", "PRODUCCION");

        Configuracion config = dao.obtener();
        if (config != null) {
            comboModo.setValue(config.getModo());
            txtRuc.setText(config.getRuc());
            txtUsuarioSunat.setText(config.getUsuarioSunat());
            txtClaveSunat.setText(config.getClaveSunat());
            txtCertificado.setText(config.getCertificadoPath());
            txtClaveCertificado.setText(config.getClaveCertificado());
            txtRazonSocial.setText(config.getRazonSocial());
            txtDireccion.setText(config.getDireccion());
            txtCorreo.setText(config.getCorreo());
            txtWeb.setText(config.getWeb());
            txtLogo.setText(config.getLogoPath());
        }
    }

    @FXML
    private void guardarConfiguracion() {
        if (comboModo.getValue() == null || comboModo.getValue().isEmpty()) {
            mostrarAlerta("Modo es obligatorio.");
            return;
        }

        Configuracion config = new Configuracion(
                comboModo.getValue(),
                txtRuc.getText(),
                txtUsuarioSunat.getText(),
                txtClaveSunat.getText(),
                txtCertificado.getText(),
                txtClaveCertificado.getText(),
                txtRazonSocial.getText(),
                txtDireccion.getText(),
                txtCorreo.getText(),
                txtWeb.getText(),
                txtLogo.getText()
        );

        dao.guardar(config);
        mostrarAlerta("Los datos han sido guardados correctamente.");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void seleccionarCertificado() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar certificado digital");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos .pfx", "*.pfx"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            txtCertificado.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void seleccionarLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar logo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            txtLogo.setText(file.getAbsolutePath());
        }
    }
}
