package com.facturador.ui;

import com.facturador.config.ApplicationContextProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainViewController {

    @FXML
    private Label mensajeBienvenida;

    @FXML
    private void abrirConfiguracion(ActionEvent event) {
        abrirVentana("/views/configuracion_form.fxml", "Configuración");
    }

    @FXML
    private void abrirClientes(ActionEvent event) {
        abrirVentana("/views/cliente_list.fxml", "Clientes");
    }

    @FXML
    private void abrirProductos(ActionEvent event) {
        abrirVentana("/views/producto_list.fxml", "Productos / Servicios");
    }

    @FXML
    private void abrirSeries(ActionEvent event) {
        abrirVentana("/views/serie_list.fxml", "Series y Numeración");
    }

    @FXML
    private void abrirFacturacion(ActionEvent event) {
        abrirVentana("/views/facturacion_form.fxml", "Facturación");
    }

    @FXML
    private void mostrarComprobantes(ActionEvent event) {
        abrirVentana("/views/comprobante_list.fxml", "Comprobantes Electrónicos");
    }

    @FXML
    private void salir(ActionEvent event) {
        System.exit(0);
    }

    private void abrirVentana(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            loader.setControllerFactory(c -> ApplicationContextProvider.getContext().getBean(c));  // inyección
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al abrir ventana: " + titulo + " -> " + e.getMessage());
            e.printStackTrace();
        }
    }
}
