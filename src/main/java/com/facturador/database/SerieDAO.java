package com.facturador.database;

import com.facturador.modelo.Serie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieDAO {

    public static List<Serie> obtenerTodas() {
        List<Serie> lista = new ArrayList<>();
        String sql = "SELECT * FROM serie";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Serie s = new Serie(
                        rs.getInt("id"),
                        rs.getString("codigo_tipo"),
                        rs.getString("tipo_comprobante"),
                        rs.getString("serie"),
                        rs.getInt("correlativo")
                );
                lista.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void actualizar(Serie s, Connection conn) throws SQLException {
        String sql = "UPDATE serie SET correlativo = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, s.getCorrelativo());
            pstmt.setInt(2, s.getId());
            pstmt.executeUpdate();
        }
    }

    public static int obtenerUltimoCorrelativo(String tipoCodigo, String serie) {
        String sql = "SELECT MAX(correlativo) as max_corr FROM comprobante WHERE tipo_comprobante = ? AND serie = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoCodigo);
            stmt.setString(2, serie);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("max_corr");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
