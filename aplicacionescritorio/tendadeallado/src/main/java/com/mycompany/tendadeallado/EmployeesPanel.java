package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class EmployeesPanel extends JPanel {
    private JLabel statusBar;
    private JTable table;
    private ConfigReader configReader;

    public EmployeesPanel(JLabel statusBar, ConfigReader configReader) {
        this.statusBar = statusBar;
        this. configReader = configReader;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titleLabel = new JLabel("Gestión de Empleados");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(new JTextField(20));
        searchPanel.add(new JButton("Buscar"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        // Columnas a mostrar (excluyendo Usuario y Contraseña)
        String[] columnNames = {"ID", "Nombre", "Rol"};

        // Cargar datos desde la base de datos
        Object[][] data = loadEmployeesFromDB();

        table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        // Añadir componentes
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Actualizar barra de estado
        if (statusBar != null) {
            statusBar.setText(" Gestión de Empleados - " + data.length + " empleados en la lista");
        }
    }

    private Object[][] loadEmployeesFromDB() {
        ArrayList<Object[]> rows = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ID_Empleado, Nombre, Rol FROM Empleados");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID_Empleado");
                String nombre = rs.getString("Nombre");
                String rol = rs.getString("Rol");

                rows.add(new Object[]{id, nombre, rol});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return rows.toArray(new Object[0][]);
    }

    public JPanel createPanel() {
        return this;
    }
}
