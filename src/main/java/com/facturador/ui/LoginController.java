package com.facturador.ui;

import com.facturador.config.ApplicationContextProvider;
import com.facturador.database.DatabaseConnection;
import com.facturador.util.SeguridadUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    @FXML
    private void ingresar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("⚠️ Usuario y clave requeridos");
            return;
        }

        String claveHash = SeguridadUtil.encriptarSHA256(clave);
        String sql = "SELECT * FROM usuario WHERE usuario = ? AND clave = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, claveHash);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
                loader.setControllerFactory(ApplicationContextProvider.getContext()::getBean);  // si deseas usar inyección aquí también

                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Facturador Electrónico");
                stage.show();

                ((Stage) txtUsuario.getScene().getWindow()).close();
            } else {
                lblMensaje.setText("❌ Usuario o clave incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("❌ Error al intentar ingresar.");
        }
    }

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
