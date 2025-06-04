package com.mycompany.tendadeallado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProvidersPanel extends JPanel {

    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;

    public ProvidersPanel(JLabel statusBar) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Gestión de Proveedores");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Contacto", "Teléfono", "Email"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProveedores = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        add(scrollPane, BorderLayout.CENTER);

        // Botonera
        JPanel botones = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        add(botones, BorderLayout.SOUTH);

        // Eventos
        btnAgregar.addActionListener(e -> mostrarFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        cargarProveedores();
    }

    private void cargarProveedores() {
        modeloTabla.setRowCount(0);
        try (Connection conn = MainFrame.getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Proveedores")) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("ID_Proveedor"),
                        rs.getString("Nombre"),
                        rs.getString("Contacto"),
                        rs.getInt("Telefono"),
                        rs.getString("Email")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarFormulario(Integer idProveedor) {
        JTextField nombreField = new JTextField();
        JTextField contactoField = new JTextField();
        JTextField telefonoField = new JTextField();
        JTextField emailField = new JTextField();

        if (idProveedor != null) {
            // Cargar datos existentes
            try (Connection conn = MainFrame.getDatabaseConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM Proveedores WHERE ID_Proveedor = ?")) {
                ps.setInt(1, idProveedor);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nombreField.setText(rs.getString("Nombre"));
                    contactoField.setText(rs.getString("Contacto"));
                    telefonoField.setText(String.valueOf(rs.getInt("Telefono")));
                    emailField.setText(rs.getString("Email"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        JPanel formPanel = new JPanel(new GridLayout(0, 1));
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Contacto:"));
        formPanel.add(contactoField);
        formPanel.add(new JLabel("Teléfono:"));
        formPanel.add(telefonoField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        int opcion = JOptionPane.showConfirmDialog(this, formPanel, idProveedor == null ? "Nuevo Proveedor" : "Editar Proveedor", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try (Connection conn = MainFrame.getDatabaseConnection()) {
                if (idProveedor == null) {
                    String sql = "INSERT INTO Proveedores (ID_Proveedor, Nombre, Contacto, Telefono, Email) VALUES (NULL, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nombreField.getText());
                        ps.setString(2, contactoField.getText());
                        ps.setInt(3, Integer.parseInt(telefonoField.getText()));
                        ps.setString(4, emailField.getText());
                        ps.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE Proveedores SET Nombre = ?, Contacto = ?, Telefono = ?, Email = ? WHERE ID_Proveedor = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nombreField.getText());
                        ps.setString(2, contactoField.getText());
                        ps.setInt(3, Integer.parseInt(telefonoField.getText()));
                        ps.setString(4, emailField.getText());
                        ps.setInt(5, idProveedor);
                        ps.executeUpdate();
                    }
                }
                cargarProveedores();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar proveedor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor para editar.");
            return;
        }
        int idProveedor = (int) modeloTabla.getValueAt(fila, 0);
        mostrarFormulario(idProveedor);
    }

    private void eliminarSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor para eliminar.");
            return;
        }
        int idProveedor = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = MainFrame.getDatabaseConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Proveedores WHERE ID_Proveedor = ?")) {
                ps.setInt(1, idProveedor);
                ps.executeUpdate();
                cargarProveedores();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
