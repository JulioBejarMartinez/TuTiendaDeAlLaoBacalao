package com.mycompany.tendadeallado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryPanel extends JPanel {
    private JLabel statusBar;
    private JTable table;
    private DefaultTableModel tableModel;

    public InventoryPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Gestión de Inventario");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Crear tabla
        String[] columnNames = {"ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JButton("Nuevo Producto"));
        buttonPanel.add(new JButton("Editar Seleccionado"));
        buttonPanel.add(new JButton("Eliminar"));
        buttonPanel.add(new JButton("Importar"));
        buttonPanel.add(new JButton("Exportar"));

        // Añadir componentes
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Cargar productos desde la base de datos
        cargarProductosDesdeBaseDeDatos();
    }

    private void cargarProductosDesdeBaseDeDatos() {
        try (Connection conn = MainFrame.getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Producto, Nombre, Descripcion, PrecioProducto, StockActual, Tipo FROM Productos")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ID_Producto"),
                    rs.getString("Nombre"),
                    rs.getString("Descripcion"),
                    rs.getDouble("PrecioProducto"),
                    rs.getInt("StockActual"),
                    rs.getString("Tipo")
                };
                tableModel.addRow(row);
            }

            if (statusBar != null) {
                statusBar.setText(" Gestión de Inventario - " + tableModel.getRowCount() + " productos en la lista");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
