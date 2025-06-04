package com.mycompany.tendadeallado;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SalesPanel extends JPanel {
    private JLabel statusBar;
    private JComboBox<String> customerComboBox;
    private JTable productTable, cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;
    private Connection connection;
    private JButton procesarPagoBtn;
    private JButton nuevaVentaBtn;
    private JButton cancelarVentaBtn;


    public SalesPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        initializePanel();
    }

    private void initializePanel() {
        removeAll();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Establecer conexión a la base de datos
        connection = MainFrame.getDatabaseConnection();

        JLabel titleLabel = new JLabel("Gestión de Ventas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nuevaVentaBtn = new JButton("Nueva Venta");
        optionsPanel.add(nuevaVentaBtn);
        topPanel.add(optionsPanel, BorderLayout.CENTER);

        JPanel mainContent = new JPanel(new GridLayout(1, 2, 10, 0));

        // PANEL DE PRODUCTOS
        JPanel productsPanel = new JPanel(new BorderLayout(0, 10));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Productos"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar producto:"));
        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton buscarBtn = new JButton("Buscar");
        searchPanel.add(buscarBtn);

        String[] productCols = {"ID", "Nombre", "Precio", "Stock"};
        DefaultTableModel productTableModel = new DefaultTableModel(productCols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // tabla productos NO editable
            }
        };
        productTable = new JTable(productTableModel);
        JScrollPane productScroll = new JScrollPane(productTable);

        productsPanel.add(searchPanel, BorderLayout.NORTH);
        productsPanel.add(productScroll, BorderLayout.CENTER);

        // PANEL DEL CARRITO
        JPanel cartPanel = new JPanel(new BorderLayout(0, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Carrito de Compra"));

        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.add(new JLabel("Cliente:"));
        customerComboBox = new JComboBox<>();
        clientPanel.add(customerComboBox);

        String[] cartCols = {"Producto", "Cantidad", "Precio Unit.", "Subtotal", "Quitar"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
        public boolean isCellEditable(int row, int column) {
        return column == 1 || column == 4;
}


            public Class<?> getColumnClass(int column) {
                if (column == 4) return JButton.class; // Para el botón "Quitar"
                if (column == 1) return Integer.class;
                if (column == 2 || column == 3) return Double.class;
                return String.class;
            }
        };

        cartTable = new JTable(cartTableModel);
        // Renderizar botón en la columna "Quitar"
        cartTable.getColumn("Quitar").setCellRenderer(new ButtonRenderer());
        cartTable.getColumn("Quitar").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane cartScroll = new JScrollPane(cartTable);

        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        totalsPanel.add(new JLabel("Total:"));
        totalLabel = new JLabel("0.00 €");
        totalsPanel.add(totalLabel);
        totalsPanel.add(new JLabel("Método de Pago:"));
        totalsPanel.add(new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"}));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelarVentaBtn = new JButton("Cancelar Venta");
        cancelarVentaBtn.setEnabled(false);  // inicialmente deshabilitado
        actionPanel.add(cancelarVentaBtn);
        cancelarVentaBtn.addActionListener(e -> {
            cartTableModel.setRowCount(0);
            actualizarTotal();
            setEditable(false);
            if (statusBar != null) {
                statusBar.setText("Venta cancelada. Pulse 'Nueva Venta' para iniciar.");
            }
        });
        procesarPagoBtn = new JButton("Procesar Pago");
        actionPanel.add(procesarPagoBtn);

        cartPanel.add(clientPanel, BorderLayout.NORTH);
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        cartPanel.add(totalsPanel, BorderLayout.SOUTH);
        cartPanel.add(actionPanel, BorderLayout.PAGE_END);

        mainContent.add(productsPanel);
        mainContent.add(cartPanel);

        add(topPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        // Estado inicial: todo deshabilitado excepto Nueva Venta
        setEditable(false);

        if (statusBar != null) {
            statusBar.setText("Gestión de Ventas - Pulse 'Nueva Venta' para comenzar");
        }

        // EVENTOS Y DATOS
        cargarClientes();
        cargarProductos(productTableModel);

        buscarBtn.addActionListener(e -> {
            if (productTable.isEnabled()) {
                buscarProductos(productTableModel, searchField.getText());
            }
        });

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (productTable.isEnabled() && e.getClickCount() == 2) {
                    int row = productTable.getSelectedRow();
                    String nombre = productTable.getValueAt(row, 1).toString();
                    double precio = Double.parseDouble(productTable.getValueAt(row, 2).toString());
                    int stock = (int) productTable.getValueAt(row, 3);
                    agregarAlCarrito(nombre, 1, precio, stock);
                }
            }
        });

        procesarPagoBtn.addActionListener(e -> procesarVenta());

        nuevaVentaBtn.addActionListener(e -> {
            // Habilitar edición y limpiar carrito para nueva venta
            cartTableModel.setRowCount(0);
            actualizarTotal();
            setEditable(true);
            if (statusBar != null) {
                statusBar.setText("Nueva venta iniciada. Añada productos y seleccione cliente.");
            }
        });

        // Detectar cambio en cantidades del carrito
        cartTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 1 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                try {
                    int nuevaCantidad = (int) cartTableModel.getValueAt(row, 1);
                    if (nuevaCantidad <= 0) {
                        // Quitar fila si cantidad <= 0
                        cartTableModel.removeRow(row);
                    } else {
                        double precioUnit = (double) cartTableModel.getValueAt(row, 2);
                        cartTableModel.setValueAt(nuevaCantidad * precioUnit, row, 3);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida");
                    // Restaurar a 1 por defecto
                    cartTableModel.setValueAt(1, row, 1);
                }
                actualizarTotal();
            }
        });
    }

    // Método para activar o desactivar componentes del formulario
    private void setEditable(boolean editable) {
        productTable.setEnabled(editable);
        customerComboBox.setEnabled(editable);
        procesarPagoBtn.setEnabled(editable);
        // También habilitar o deshabilitar búsqueda y botones relacionados
        // Por simplicidad, iterar componentes y bloquear el campo búsqueda y botones
        for (Component c : ((JPanel)((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER)).getComponents()) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel) c).getComponents()) {
                    if (inner instanceof JPanel) {
                        for (Component comp : ((JPanel) inner).getComponents()) {
                            if (comp instanceof JTextField) comp.setEnabled(editable);
                            if (comp instanceof JButton && !comp.equals(nuevaVentaBtn)) comp.setEnabled(editable);
                        }
                    }
                }
            }
        }
        cartTable.setEnabled(editable);
    }

    private void cargarClientes() {
        customerComboBox.removeAllItems();
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT ID_Cliente, Nombre, Apellido FROM Clientes");
            while (rs.next()) {
                int id = rs.getInt("ID_Cliente");
                String nombre = rs.getString("Nombre") + " " + rs.getString("Apellido");
                customerComboBox.addItem(id + " - " + nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando clientes: " + e.getMessage());
        }
    }

    private void cargarProductos(DefaultTableModel model) {
        model.setRowCount(0);
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT ID_Producto, Nombre, PrecioProducto, StockActual FROM Productos");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ID_Producto"),
                        rs.getString("Nombre"),
                        rs.getDouble("PrecioProducto"),
                        rs.getInt("StockActual")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando productos: " + e.getMessage());
        }
    }

    private void buscarProductos(DefaultTableModel model, String termino) {
        model.setRowCount(0);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT ID_Producto, Nombre, PrecioProducto, StockActual FROM Productos WHERE nombre LIKE ?")) {
            ps.setString(1, "%" + termino + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ID_Producto"),
                        rs.getString("Nombre"),
                        rs.getDouble("PrecioProducto"),
                        rs.getInt("StockActual")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en búsqueda: " + e.getMessage());
        }
    }

    private void agregarAlCarrito(String producto, int cantidad, double precioUnit, int stockDisponible) {
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            String prodEnCarrito = (String) cartTableModel.getValueAt(i, 0);
            if (prodEnCarrito.equals(producto)) {
                int cantidadActual = (int) cartTableModel.getValueAt(i, 1);
                int nuevaCantidad = cantidadActual + cantidad;
                if (nuevaCantidad > stockDisponible) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente para " + producto);
                    return;
                }
                cartTableModel.setValueAt(nuevaCantidad, i, 1);
                cartTableModel.setValueAt(nuevaCantidad * precioUnit, i, 3);
                actualizarTotal();
                return;
            }
        }
        if (cantidad > stockDisponible) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente para " + producto);
            return;
        }
        double subtotal = cantidad * precioUnit;
        // Añadir botón "Quitar" vacío, se renderiza como botón
        cartTableModel.addRow(new Object[]{producto, cantidad, precioUnit, subtotal, "X"});
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total += (double) cartTableModel.getValueAt(i, 3);
        }
        totalLabel.setText(String.format("%.2f €", total));
    }

    private void procesarVenta() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }
        try {
            connection.setAutoCommit(false);

            String clienteSel = (String) customerComboBox.getSelectedItem();
            if (clienteSel == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
                return;
            }
            int clienteId = Integer.parseInt(clienteSel.split(" - ")[0]);
            int empleadoId = 1;

            PreparedStatement psVenta = connection.prepareStatement(
                    "INSERT INTO Ventas (FechaHora, ID_Empleado, ID_Cliente, Total) VALUES (NOW(), ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, empleadoId);
            psVenta.setInt(2, clienteId);
            psVenta.setDouble(3, calcularTotal());
            psVenta.executeUpdate();

            ResultSet generated = psVenta.getGeneratedKeys();
            generated.next();
            int ventaId = generated.getInt(1);

            PreparedStatement psDetalle = connection.prepareStatement(
                    "INSERT INTO DetallesVenta (ID_Venta, ID_Producto, Cantidad) VALUES (?, ?, ?)");

            PreparedStatement psStock = connection.prepareStatement(
                    "UPDATE Productos SET StockActual = StockActual - ? WHERE ID_Producto = ?");

            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                String nombreProducto = (String) cartTableModel.getValueAt(i, 0);
                int cantidad = (int) cartTableModel.getValueAt(i, 1);
                int productoId = obtenerIdProductoPorNombre(nombreProducto);

                if (productoId == -1) throw new SQLException("Producto no encontrado: " + nombreProducto);

                psDetalle.setInt(1, ventaId);
                psDetalle.setInt(2, productoId);
                psDetalle.setInt(3, cantidad);
                psDetalle.addBatch();

                psStock.setInt(1, cantidad);
                psStock.setInt(2, productoId);
                psStock.addBatch();
            }
            psDetalle.executeBatch();
            psStock.executeBatch();

            connection.commit();

            JOptionPane.showMessageDialog(this, "Venta registrada con éxito (ID: " + ventaId + ")");
            cartTableModel.setRowCount(0);
            actualizarTotal();

            // Deshabilitar otra vez tras procesar
            setEditable(false);
            if (statusBar != null) {
                statusBar.setText("Gestión de Ventas - Venta finalizada. Pulse 'Nueva Venta' para comenzar otra.");
            }

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error procesando venta: " + e.getMessage());
        }
    }

    private int obtenerIdProductoPorNombre(String nombre) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT ID_Producto FROM Productos WHERE Nombre = ?")) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Producto");
            }
        }
        return -1;
    }

    private double calcularTotal() {
        double total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total += (double) cartTableModel.getValueAt(i, 3);
        }
        return total;
    }

    // Renderizador para el botón "Quitar"
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("X");
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Editor para el botón "Quitar"
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("X");
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "X" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                // Quitar fila del carrito
                cartTableModel.removeRow(row);
                actualizarTotal();
            }
            clicked = false;
            return label;
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
