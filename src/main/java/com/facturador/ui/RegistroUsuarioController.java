package com.facturador.ui;

import com.facturador.database.DatabaseConnection;
import com.facturador.util.SeguridadUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistroUsuarioController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    // Método para registrar un nuevo usuario
    @FXML
    private void registrarUsuario(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("⚠️ Complete todos los campos");
            return;
        }

        String claveHash = SeguridadUtil.hashPassword(clave);
        String sql = "INSERT INTO usuario (usuario, clave) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, claveHash);
            pstmt.executeUpdate();

            lblMensaje.setText("✅ Usuario registrado correctamente.");
            txtUsuario.clear();
            txtClave.clear();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("❌ Error al registrar usuario.");
        }
    }

    // Botones tipo calculadora para ingresar clave
    @FXML
    private void agregarDigito(ActionEvent event) {
        Button boton = (Button) event.getSource();
        txtClave.setText(txtClave.getText() + boton.getText());
    }

    @FXML
    private void limpiarClave() {
        txtClave.clear();
    }

    @FXML
    private void retrocederClave() {
        String actual = txtClave.getText();
        if (!actual.isEmpty()) {
            txtClave.setText(actual.substring(0, actual.length() - 1));
        }
    }
}
