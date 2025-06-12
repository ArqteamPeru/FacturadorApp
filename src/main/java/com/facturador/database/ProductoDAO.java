package com.facturador.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public static void insertarProducto(Producto producto) {
        String sql = "INSERT INTO producto (codigo, descripcion, unidad, precio_unit, tipo_afectacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setString(3, producto.getUnidad());
            pstmt.setDouble(4, producto.getPrecioUnit());
            pstmt.setString(5, producto.getTipoAfectacion());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void actualizarProducto(Producto producto) {
        String sql = "UPDATE producto SET descripcion = ?, unidad = ?, precio_unit = ?, tipo_afectacion = ? WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getDescripcion());
            pstmt.setString(2, producto.getUnidad());
            pstmt.setDouble(3, producto.getPrecioUnit());
            pstmt.setString(4, producto.getTipoAfectacion());
            pstmt.setString(5, producto.getCodigo());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generarCodigo(String tipo) {
        String prefijo = tipo.equalsIgnoreCase("SERVICIO") ? "S" : "P";
        String sql = "SELECT COUNT(*) FROM producto WHERE codigo LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, prefijo + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("%s%04d", prefijo, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return prefijo + "0001";
    }

    public static List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getString("unidad"),
                        rs.getDouble("precio_unit"),
                        rs.getString("tipo_afectacion")
                );
                lista.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static void eliminarProducto(String codigo) {
        String sql = "DELETE FROM producto WHERE codigo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
