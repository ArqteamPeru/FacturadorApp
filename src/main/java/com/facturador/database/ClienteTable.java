package com.facturador.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ClienteTable {

    public static void crearTabla() {
        String sql = """
            CREATE TABLE IF NOT EXISTS cliente (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo_doc TEXT,
                numero_doc TEXT,
                razon_social TEXT,
                direccion TEXT
            );
        """;

        try (Connection conn = DatabaseConnection.getConnection();
                          Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabla 'cliente' creada o ya existe.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
