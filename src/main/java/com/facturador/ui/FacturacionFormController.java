package com.facturador.ui;

import com.facturador.database.*;
import com.facturador.modelo.*;
import com.facturador.util.ComboItem;
import com.facturador.util.NumeroALetrasUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FacturacionFormController {

    @FXML private ComboBox<ComboItem> tipoComprobanteCombo;
    @FXML private TextField rucField, razonSocialField, direccionField, serieField, correlativoField;
    @FXML private ComboBox<String> tipoOperacionCombo, condicionPagoCombo, metodoPagoCombo;
    @FXML private DatePicker fechaEmisionPicker, fechaVencimientoPicker;
    @FXML private TableView<ItemDetalle> detalleTable;
    @FXML private TableColumn<ItemDetalle, String> descripcionColumn;
    @FXML private TableColumn<ItemDetalle, Number> cantidadColumn, precioColumn, totalColumn;
    @FXML private Label subtotalLabel, igvLabel, totalLabel, totalTextoLabel;
    @FXML private CheckBox conDetraccionCheckBox;
    @FXML private TextField codigoBienDetraccionField, porcentajeDetraccionField, montoDetraccionField, cuentaBancoNacionField, leyendaDetraccionField;
    @FXML private ComboBox<String> medioPagoDetraccionCombo;

    private final ObservableList<ItemDetalle> detalles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarCatalogos();
        configurarColumnasEditables();
        fechaEmisionPicker.setValue(LocalDate.now());
        fechaVencimientoPicker.setValue(LocalDate.now());

        descripcionColumn.setCellValueFactory(c -> c.getValue().descripcionProperty());
        cantidadColumn.setCellValueFactory(c -> c.getValue().cantidadProperty());
        precioColumn.setCellValueFactory(c -> c.getValue().precioProperty());
        totalColumn.setCellValueFactory(c -> c.getValue().totalProperty());
        detalleTable.setItems(detalles);
        detalleTable.setEditable(true);

        tipoComprobanteCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> actualizarSerieYCorrelativo(newVal));
    }

    private void cargarCatalogos() {
        tipoComprobanteCombo.setItems(FXCollections.observableArrayList(CatalogoDAO.obtenerPorTabla("catalogo_tipo_comprobante").stream().map(c -> new ComboItem(c.getCodigo(), c.getDescripcion())).collect(Collectors.toList())));
        tipoOperacionCombo.setItems(FXCollections.observableArrayList(CatalogoDAO.obtenerPorTabla("catalogo_tipo_operacion").stream().map(Catalogo::getDescripcion).collect(Collectors.toList())));
        condicionPagoCombo.setItems(FXCollections.observableArrayList(CatalogoDAO.obtenerPorTabla("catalogo_condicion_pago").stream().map(Catalogo::getDescripcion).collect(Collectors.toList())));
        metodoPagoCombo.setItems(FXCollections.observableArrayList(CatalogoDAO.obtenerPorTabla("catalogo_metodo_pago").stream().map(Catalogo::getDescripcion).collect(Collectors.toList())));
        medioPagoDetraccionCombo.setItems(FXCollections.observableArrayList("003 - Transferencia de fondos"));
    }

    private void configurarColumnasEditables() {
        cantidadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            public String toString(Number n) { return n.toString(); }
            public Number fromString(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0; } }
        }));
        cantidadColumn.setOnEditCommit(e -> { e.getRowValue().setCantidad(e.getNewValue().doubleValue()); recalcularTotales(); });

        precioColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            public String toString(Number n) { return n.toString(); }
            public Number fromString(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0; } }
        }));
        precioColumn.setOnEditCommit(e -> { e.getRowValue().setPrecio(e.getNewValue().doubleValue()); recalcularTotales(); });
    }

    private void actualizarSerieYCorrelativo(ComboItem seleccionado) {
        if (seleccionado == null) return;
        for (Serie s : SerieDAO.obtenerTodas()) {
            if (s.getCodigoTipo().equalsIgnoreCase(seleccionado.getCodigo())) {
                int siguiente = SerieDAO.obtenerUltimoCorrelativo(s.getCodigoTipo(), s.getSerie()) + 1;
                serieField.setText(s.getSerie());
                correlativoField.setText(String.format("%08d", siguiente));
                break;
            }
        }
    }

    private void recalcularTotales() {
        double subtotal = detalles.stream().mapToDouble(ItemDetalle::getTotal).sum();
        double igv = subtotal * 0.18;
        double total = subtotal + igv;
        subtotalLabel.setText(String.format("%.2f", subtotal));
        igvLabel.setText(String.format("%.2f", igv));
        totalLabel.setText(String.format("%.2f", total));
        totalTextoLabel.setText(NumeroALetrasUtil.convertir(total).toUpperCase());
    }

    private double parseDoubleSafe(String texto) {
        try { return Double.parseDouble(texto); } catch (Exception e) { return 0.0; }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        rucField.clear(); razonSocialField.clear(); direccionField.clear(); detalles.clear();
        subtotalLabel.setText("0.00"); igvLabel.setText("0.00"); totalLabel.setText("0.00");
        totalTextoLabel.setText("SON: CERO CON 00/100 SOLES"); conDetraccionCheckBox.setSelected(false);
        codigoBienDetraccionField.clear(); porcentajeDetraccionField.clear(); montoDetraccionField.clear(); cuentaBancoNacionField.clear(); leyendaDetraccionField.clear();
    }

    @FXML
    private void buscarClientePorRUC(KeyEvent event) {
        String ruc = rucField.getText();
        if (ruc.length() >= 8) {
            ClienteDAO.buscarPorNumero(ruc).ifPresent(cliente -> {
                razonSocialField.setText(cliente.getRazonSocial());
                direccionField.setText(cliente.getDireccion());
            });
        }
    }

    @FXML
    private void agregarItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/seleccionar_producto.fxml"));
            Parent root = loader.load();
            SeleccionarProductoController controller = loader.getController();
            controller.setListener(producto -> {
                ItemDetalle nuevoItem = new ItemDetalle(
                        producto.getCodigo(),
                        producto.getDescripcion(),
                        producto.getUnidad(),
                        1.0,
                        producto.getPrecioUnit()
                );
                nuevoItem.setTipoAfectacion(producto.getTipoAfectacion());
                detalles.add(nuevoItem);
                recalcularTotales();
            });

            Stage stage = new Stage();
            stage.setTitle("Seleccionar Producto o Servicio");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir ventana de productos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarItemSeleccionado() {
        ItemDetalle seleccionado = detalleTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            detalles.remove(seleccionado);
            recalcularTotales();
        } else {
            mostrarAlerta("Seleccione un ítem de la tabla para eliminar.");
        }
    }

    @FXML
    private void guardarFactura() {
        ComboItem tipoComprobante = tipoComprobanteCombo.getValue();
        if (tipoComprobante == null || rucField.getText().isEmpty()) {
            mostrarAlerta("Debe completar los campos obligatorios.");
            return;
        }

        Comprobante comp = new Comprobante(
                tipoComprobante.getCodigo(),
                serieField.getText(),
                correlativoField.getText(),
                rucField.getText(),
                razonSocialField.getText(),
                direccionField.getText(),
                fechaEmisionPicker.getValue(),
                fechaVencimientoPicker.getValue(),
                tipoOperacionCombo.getValue(),
                condicionPagoCombo.getValue(),
                metodoPagoCombo.getValue(),
                Double.parseDouble(totalLabel.getText()),
                detalles
        );

        if (conDetraccionCheckBox.isSelected()) {
            comp.setConDetraccion(true);
            comp.setCodigoBienDetraccion(codigoBienDetraccionField.getText());
            comp.setPorcentajeDetraccion(parseDoubleSafe(porcentajeDetraccionField.getText()));
            comp.setMontoDetraccion(parseDoubleSafe(montoDetraccionField.getText()));
            comp.setCuentaBancoNacion(cuentaBancoNacionField.getText());
            String medio = medioPagoDetraccionCombo.getValue();
            if (medio != null && medio.contains("-")) {
                comp.setMedioPagoDetraccion(medio.split("-")[0].trim()); // Ej: "003"
            }

            comp.setLeyendaDetraccion(leyendaDetraccionField.getText());
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String sqlCab = "INSERT INTO comprobante (tipo_comprobante, serie, correlativo, fecha_emision, fecha_vencimiento, tipo_operacion, condicion_pago, metodo_pago, ruc_dni, razon_social, direccion, total, estado, con_detraccion, codigo_bien_detraccion, porcentaje_detraccion, monto_detraccion, cuenta_bn, medio_pago_detraccion, leyenda_detraccion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCab, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, comp.getTipoComprobante());
                pstmt.setString(2, comp.getSerie());
                pstmt.setInt(3, Integer.parseInt(comp.getCorrelativo()));
                pstmt.setString(4, comp.getFechaEmision().toString());
                pstmt.setString(5, comp.getFechaVencimiento().toString());
                pstmt.setString(6, comp.getTipoOperacion());
                pstmt.setString(7, comp.getCondicionPago());
                pstmt.setString(8, comp.getMetodoPago());
                pstmt.setString(9, comp.getNumeroDocumento());
                pstmt.setString(10, comp.getRazonSocial());
                pstmt.setString(11, comp.getDireccion());
                pstmt.setDouble(12, comp.getTotal());
                pstmt.setString(13, "PENDIENTE");
                pstmt.setBoolean(14, comp.isConDetraccion());
                pstmt.setString(15, comp.getCodigoBienDetraccion());
                pstmt.setDouble(16, comp.getPorcentajeDetraccion() != null ? comp.getPorcentajeDetraccion() : 0.0);
                pstmt.setDouble(17, comp.getMontoDetraccion() != null ? comp.getMontoDetraccion() : 0.0);
                pstmt.setString(18, comp.getCuentaBancoNacion());
                pstmt.setString(19, comp.getMedioPagoDetraccion());
                pstmt.setString(20, comp.getLeyendaDetraccion());
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int compId = rs.getInt(1);
                    String sqlDet = "INSERT INTO detalle_comprobante (comprobante_id, descripcion, unidad, cantidad, precio_unit, tipo_afectacion, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pdet = conn.prepareStatement(sqlDet)) {
                        for (ItemDetalle item : comp.getDetalles()) {
                            pdet.setInt(1, compId);
                            pdet.setString(2, item.getDescripcion());
                            pdet.setString(3, item.getUnidad());
                            pdet.setDouble(4, item.getCantidad());
                            pdet.setDouble(5, item.getPrecio());
                            pdet.setString(6, item.getTipoAfectacion());
                            pdet.setDouble(7, item.getSubtotal());
                            pdet.addBatch();
                        }
                        pdet.executeBatch();
                    }
                }
            }

            for (Serie s : SerieDAO.obtenerTodas()) {
                if (s.getCodigoTipo().equals(comp.getTipoComprobante()) && s.getSerie().equals(comp.getSerie())) {
                    s.setCorrelativo(s.getCorrelativo() + 1);
                    SerieDAO.actualizar(s, conn);
                    break;
                }
            }

            conn.commit();
            mostrarInfo("Comprobante guardado correctamente.");
            limpiarFormulario();
            actualizarSerieYCorrelativo(tipoComprobante);
        } catch (Exception e) {
            mostrarAlerta("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
