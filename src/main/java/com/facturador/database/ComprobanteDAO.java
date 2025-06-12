package com.facturador.database;

import com.facturador.modelo.Comprobante;
import com.facturador.modelo.ItemDetalle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComprobanteDAO {

    public static List<Comprobante> obtenerTodos() {
        List<Comprobante> lista = new ArrayList<>();

        String sql = "SELECT * FROM comprobante";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo_comprobante");
                String serie = rs.getString("serie");
                String correlativo = String.valueOf(rs.getInt("correlativo"));
                String razonSocial = rs.getString("razon_social");
                String rucDni = rs.getString("ruc_dni");
                double total = rs.getDouble("total");
                LocalDate fechaEmision = LocalDate.parse(rs.getString("fecha_emision"));
                String estado = rs.getString("estado");
                String direccion = rs.getString("direccion");

                boolean conDetraccion = rs.getBoolean("con_detraccion");
                String codigoBienDetraccion = rs.getString("codigo_bien_detraccion");
                Double porcentajeDetraccion = rs.getObject("porcentaje_detraccion") != null ? rs.getDouble("porcentaje_detraccion") : null;
                Double montoDetraccion = rs.getObject("monto_detraccion") != null ? rs.getDouble("monto_detraccion") : null;
                String cuentaBancoNacion = rs.getString("cuenta_bn");
                String medioPagoDetraccion = rs.getString("medio_pago_detraccion");
                String leyendaDetraccion = rs.getString("leyenda_detraccion");

                // Carga de detalles asociados
                List<ItemDetalle> detalles = obtenerDetallesPorComprobanteId(id);

                Comprobante c = new Comprobante(
                        tipo,
                        serie,
                        correlativo,
                        rucDni,
                        razonSocial,
                        direccion != null ? direccion : "",
                        fechaEmision,
                        null,
                        "", "", "",
                        total,
                        detalles
                );

                c.setEstado(estado);
                c.setConDetraccion(conDetraccion);
                c.setCodigoBienDetraccion(codigoBienDetraccion);
                c.setPorcentajeDetraccion(porcentajeDetraccion);
                c.setMontoDetraccion(montoDetraccion);
                c.setCuentaBancoNacion(cuentaBancoNacion);
                c.setMedioPagoDetraccion(medioPagoDetraccion);
                c.setLeyendaDetraccion(leyendaDetraccion);
                lista.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener comprobantes: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    private static List<ItemDetalle> obtenerDetallesPorComprobanteId(int comprobanteId) {
        List<ItemDetalle> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_comprobante WHERE comprobante_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, comprobanteId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String descripcion = rs.getString("descripcion");
                String unidad = rs.getString("unidad");
                double cantidad = rs.getDouble("cantidad");
                double precio = rs.getDouble("precio_unit");

                ItemDetalle item = new ItemDetalle("", descripcion, unidad, cantidad, precio);
                detalles.add(item);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener detalles: " + e.getMessage());
            e.printStackTrace();
        }

        return detalles;
    }

    public static void actualizarEstado(Comprobante comprobante) {
        String sql = "UPDATE comprobante SET estado = ? WHERE serie = ? AND correlativo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, comprobante.getEstado());
            pstmt.setString(2, comprobante.getSerie());
            pstmt.setInt(3, Integer.parseInt(comprobante.getCorrelativo()));

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                System.out.println("⚠️ No se encontró el comprobante para actualizar.");
            } else {
                System.out.println("✅ Estado actualizado en la base de datos.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
