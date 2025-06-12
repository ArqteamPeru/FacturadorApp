package com.facturador.database;

import com.facturador.modelo.Catalogo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {

    private static final List<String> tablasPermitidas = List.of(
            "catalogo_tipo_comprobante",
            "catalogo_tipo_documento_identidad",
            "catalogo_afectacion_igv",
            "catalogo_unidad_medida",
            "catalogo_tipo_operacion",
            "catalogo_metodo_pago",
            "catalogo_tipo_nota_credito",
            "catalogo_tipo_nota_debito",
            "catalogo_condicion_pago"
    );

    public static List<Catalogo> obtenerPorTabla(String nombreTabla) {
        List<Catalogo> lista = new ArrayList<>();

        // Verificación para evitar inyecciones SQL
        if (!tablasPermitidas.contains(nombreTabla)) {
            System.err.println("❌ Acceso a tabla no permitida: " + nombreTabla);
            return lista;
        }

        String sql = "SELECT codigo, descripcion FROM " + nombreTabla;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Catalogo(rs.getString("codigo"), rs.getString("descripcion")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
