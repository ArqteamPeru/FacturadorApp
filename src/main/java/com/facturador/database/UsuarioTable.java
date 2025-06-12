package com.facturador.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioTable {
    public static void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT UNIQUE NOT NULL," +
                "clave TEXT NOT NULL," +
                "nombre TEXT NOT NULL," +
                "rol TEXT NOT NULL" +
                ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabla 'usuario' creada o ya existe.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla usuario: " + e.getMessage());
        }
    }
}
