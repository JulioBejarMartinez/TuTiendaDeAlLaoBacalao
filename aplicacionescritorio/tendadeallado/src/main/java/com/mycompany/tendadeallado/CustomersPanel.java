package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class CustomersPanel extends JPanel {
    private JLabel statusBar;
    private JTable table;

    public CustomersPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titleLabel = new JLabel("Gestión de Clientes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(new JTextField(20));
        searchPanel.add(new JButton("Buscar"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        // Columnas de la tabla
        String[] columnNames = {"ID", "Nombre", "Apellido", "Email", "Teléfono", "Puntos de Fidelidad"};

        // Cargar datos desde DB
        Object[][] data = loadCustomersFromDB();

        table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        // Añadir componentes
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Actualizar barra de estado
        if (statusBar != null) {
            statusBar.setText(" Gestión de Clientes - " + data.length + " clientes en la lista");
        }
    }

    private Object[][] loadCustomersFromDB() {
        ArrayList<Object[]> rows = new ArrayList<>();

        try (Connection conn = MainFrame.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ID_Cliente, Nombre, Apellido, Email, Telefono, PuntosFidelidad FROM Clientes");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID_Cliente");
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                String email = rs.getString("Email");
                String telefono = rs.getString("Telefono") != null ? rs.getString("Telefono") : "";
                int puntos = rs.getInt("PuntosFidelidad");

                rows.add(new Object[]{id, nombre, apellido, email, telefono, puntos});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return rows.toArray(new Object[0][]);
    }

    public JPanel createPanel() {
        return this;
    }
}
