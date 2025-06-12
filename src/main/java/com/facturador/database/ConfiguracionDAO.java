package com.facturador.database;

import com.facturador.modelo.Configuracion;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class ConfiguracionDAO {

    private Connection conectar() {
        String url = "jdbc:sqlite:facturador.db";
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }

    public void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS configuracion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "modo TEXT, " +
                "ruc TEXT, " +
                "usuario_sunat TEXT, " +
                "clave_sunat TEXT, " +
                "certificado_path TEXT, " +
                "clave_certificado TEXT, " +
                "razon_social TEXT, " +
                "direccion TEXT, " +
                "correo TEXT, " +
                "web TEXT, " +
                "logo_path TEXT" +
                ")";
        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabla 'configuracion' verificada o creada.");
        } catch (SQLException e) {
            System.err.println("❌ Error al crear tabla 'configuracion': " + e.getMessage());
        }
    }

    public void guardar(Configuracion config) {
        String deleteSql = "DELETE FROM configuracion";
        String insertSql = "INSERT INTO configuracion (" +
                "modo, ruc, usuario_sunat, clave_sunat, certificado_path, clave_certificado, " +
                "razon_social, direccion, correo, web, logo_path" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = conectar();
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                PreparedStatement pstmt = conn.prepareStatement(insertSql)
        ) {
            deleteStmt.executeUpdate();

            pstmt.setString(1, config.getModo());
            pstmt.setString(2, config.getRuc());
            pstmt.setString(3, config.getUsuarioSunat());
            pstmt.setString(4, config.getClaveSunat());
            pstmt.setString(5, config.getCertificadoPath());
            pstmt.setString(6, config.getClaveCertificado());
            pstmt.setString(7, config.getRazonSocial());
            pstmt.setString(8, config.getDireccion());
            pstmt.setString(9, config.getCorreo());
            pstmt.setString(10, config.getWeb());
            pstmt.setString(11, config.getLogoPath());

            pstmt.executeUpdate();
            System.out.println("✅ Configuración guardada correctamente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar configuración: " + e.getMessage());
        }
    }

    public Configuracion obtener() {
        String sql = "SELECT * FROM configuracion LIMIT 1";

        try (
                Connection conn = conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            if (rs.next()) {
                return new Configuracion(
                        rs.getString("modo"),
                        rs.getString("ruc"),
                        rs.getString("usuario_sunat"),
                        rs.getString("clave_sunat"),
                        rs.getString("certificado_path"),
                        rs.getString("clave_certificado"),
                        rs.getString("razon_social"),
                        rs.getString("direccion"),
                        rs.getString("correo"),
                        rs.getString("web"),
                        rs.getString("logo_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener configuración: " + e.getMessage());
        }

        return null;
    }
}
