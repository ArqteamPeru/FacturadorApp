package com.facturador;

import com.facturador.database.DatabaseConnection;
import com.facturador.util.SeguridadUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.*;

public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SpringBootFacturadorApp.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        crearTablasSiNoExisten();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Login - Facturador Electrónico");
        stage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    private void crearTablasSiNoExisten() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS cliente (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "tipo_doc TEXT," +
                    "numero_doc TEXT," +
                    "razon_social TEXT," +
                    "direccion TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS producto (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "codigo TEXT," +
                    "descripcion TEXT," +
                    "unidad TEXT," +
                    "precio REAL," +
                    "afectacion TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS serie (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "codigo_tipo TEXT NOT NULL," +
                    "tipo_comprobante TEXT NOT NULL," +
                    "serie TEXT NOT NULL," +
                    "correlativo INTEGER NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS configuracion (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "modo TEXT," +
                    "ruc TEXT," +
                    "usuario_sunat TEXT," +
                    "clave_sunat TEXT," +
                    "certificado_path TEXT," +
                    "clave_certificado TEXT," +
                    "razon_social TEXT," +
                    "direccion TEXT," +
                    "correo TEXT," +
                    "web TEXT," +
                    "logo_path TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS usuario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario TEXT UNIQUE NOT NULL," +
                    "clave TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS comprobante (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "tipo_comprobante TEXT NOT NULL," +
                    "serie TEXT NOT NULL," +
                    "correlativo INTEGER NOT NULL," +
                    "fecha_emision TEXT NOT NULL," +
                    "fecha_vencimiento TEXT," +
                    "tipo_operacion TEXT NOT NULL," +
                    "condicion_pago TEXT NOT NULL," +
                    "metodo_pago TEXT NOT NULL," +
                    "ruc_dni TEXT NOT NULL," +
                    "razon_social TEXT NOT NULL," +
                    "direccion TEXT," +
                    "total REAL NOT NULL," +
                    "estado TEXT DEFAULT 'PENDIENTE')");

            stmt.execute("CREATE TABLE IF NOT EXISTS detalle_comprobante (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "comprobante_id INTEGER NOT NULL," +
                    "descripcion TEXT NOT NULL," +
                    "unidad TEXT NOT NULL," +
                    "cantidad REAL NOT NULL," +
                    "precio_unit REAL NOT NULL," +
                    "tipo_afectacion TEXT NOT NULL," +
                    "subtotal REAL NOT NULL," +
                    "FOREIGN KEY (comprobante_id) REFERENCES comprobante(id))");

            DatabaseMetaData meta = conn.getMetaData();

            ResultSet rsEstado = meta.getColumns(null, null, "comprobante", "estado");
            if (!rsEstado.next()) {
                stmt.execute("ALTER TABLE comprobante ADD COLUMN estado TEXT DEFAULT 'PENDIENTE'");
            }

            // ✅ Campos adicionales para detracción
            ResultSet rsDetraccion = meta.getColumns(null, null, "comprobante", "con_detraccion");
            if (!rsDetraccion.next()) {
                stmt.execute("ALTER TABLE comprobante ADD COLUMN con_detraccion INTEGER DEFAULT 0");
                stmt.execute("ALTER TABLE comprobante ADD COLUMN codigo_bien_detraccion TEXT");
                stmt.execute("ALTER TABLE comprobante ADD COLUMN porcentaje_detraccion REAL");
                stmt.execute("ALTER TABLE comprobante ADD COLUMN monto_detraccion REAL");
                stmt.execute("ALTER TABLE comprobante ADD COLUMN cuenta_bn TEXT");
                System.out.println("✅ Campos de detracción agregados.");
            }

            // ✅ Nuevos campos requeridos por SUNAT para la detracción
            ResultSet rsMedioPago = meta.getColumns(null, null, "comprobante", "medio_pago_detraccion");
            if (!rsMedioPago.next()) {
                stmt.execute("ALTER TABLE comprobante ADD COLUMN medio_pago_detraccion TEXT");
                System.out.println("✅ Campo 'medio_pago_detraccion' agregado.");
            }

            ResultSet rsLeyenda = meta.getColumns(null, null, "comprobante", "leyenda_detraccion");
            if (!rsLeyenda.next()) {
                stmt.execute("ALTER TABLE comprobante ADD COLUMN leyenda_detraccion TEXT");
                System.out.println("✅ Campo 'leyenda_detraccion' agregado.");
            }

            // Usuario admin
            ResultSet rs = stmt.executeQuery("SELECT * FROM usuario WHERE usuario = 'admin'");
            if (!rs.next()) {
                String claveEncriptada = SeguridadUtil.hashPassword("2011");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO usuario (usuario, clave) VALUES (?, ?)")) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, claveEncriptada);
                    pstmt.executeUpdate();
                    System.out.println("✅ Usuario 'admin' creado por defecto.");
                }
            } else if (SeguridadUtil.isLegacyHash(rs.getString("clave"))) {
                String nueva = SeguridadUtil.hashPassword("2011");
                try (PreparedStatement upd = conn.prepareStatement("UPDATE usuario SET clave = ? WHERE id = ?")) {
                    upd.setString(1, nueva);
                    upd.setInt(2, rs.getInt("id"));
                    upd.executeUpdate();
                    System.out.println("ℹ️ Contraseña de 'admin' actualizada al nuevo formato.");
                }
            } else {
                System.out.println("ℹ️ Usuario 'admin' ya existe.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al crear tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
