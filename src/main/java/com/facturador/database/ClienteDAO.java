package com.facturador.database;

import com.facturador.database.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class ClienteDAO {

    public static Optional<Cliente> buscarPorNumero(String numeroDoc) {
        String sql = "SELECT tipo_doc, numero_doc, razon_social, direccion FROM cliente WHERE numero_doc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroDoc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getString("tipo_doc"),
                        rs.getString("numero_doc"),
                        rs.getString("razon_social"),
                        rs.getString("direccion")
                );
                return Optional.of(cliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static void insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente (tipo_doc, numero_doc, razon_social, direccion) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getTipoDoc());
            stmt.setString(2, cliente.getNumeroDoc());
            stmt.setString(3, cliente.getRazonSocial());
            stmt.setString(4, cliente.getDireccion());

            stmt.executeUpdate();
            System.out.println("✅ Cliente insertado en la base de datos.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
