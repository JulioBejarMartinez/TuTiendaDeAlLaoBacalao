package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomePanel extends JPanel {

   final private JLabel ventasLabel;
   final private JLabel clientesLabel;
   final private JLabel productosLabel;
   final private JLabel alertasLabel;

    public HomePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titleLabel = new JLabel("Dashboard - Resumen del Sistema");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Panel de estadísticas con referencias a los labels para actualizar datos reales
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));

        ventasLabel = new JLabel("Cargando...");
        clientesLabel = new JLabel("Cargando...");
        productosLabel = new JLabel("Cargando...");
        alertasLabel = new JLabel("Cargando...");

        statsPanel.add(createStatsCard("Ventas del día", ventasLabel, "↑ 12%"));
        statsPanel.add(createStatsCard("Clientes activos", clientesLabel, "↑ 5%"));
        statsPanel.add(createStatsCard("Productos en stock", productosLabel, ""));
        statsPanel.add(createStatsCard("Alertas de inventario", alertasLabel, "⚠"));

        // Panel de acciones rápidas
        JPanel quickActionsPanel = new JPanel();
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Acciones Rápidas"));
        quickActionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        quickActionsPanel.add(createQuickActionButton("Nueva Venta"));
        quickActionsPanel.add(createQuickActionButton("Agregar Producto"));
        quickActionsPanel.add(createQuickActionButton("Registrar Cliente"));
        quickActionsPanel.add(createQuickActionButton("Generar Informe"));

        // Añadir componentes al panel principal (this)
        add(titleLabel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(quickActionsPanel, BorderLayout.SOUTH);

        // Cargar datos reales
        cargarDatosBD();
    }

    private JPanel createStatsCard(String title, JLabel valueLabel, String change) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel changeLabel = new JLabel(change);
        if (change.contains("↑")) {
            changeLabel.setForeground(new Color(0, 150, 0));
        } else if (change.contains("↓")) {
            changeLabel.setForeground(new Color(150, 0, 0));
        }

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(changeLabel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createQuickActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        return button;
    }

    private void cargarDatosBD() {
        // Ejecutar en un hilo aparte para no bloquear la UI
        new Thread(() -> {
            try {
                // Conexión a la base de datos usando el metodo conexion desde MainFrame
                Connection conn = MainFrame.getDatabaseConnection();
                if (conn == null) {
                    SwingUtilities.invokeLater(() -> {
                        ventasLabel.setText("Error de conexión");
                        clientesLabel.setText("Error de conexión");
                        productosLabel.setText("Error de conexión");
                        alertasLabel.setText("Error de conexión");
                    });
                    return;
                }

                // Ventas del día (sumar Total en tabla Ventas con FechaHora hoy)
                String fechaHoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                String sqlVentas = "SELECT COALESCE(SUM(Total),0) FROM Ventas WHERE DATE(FechaHora) = ?";
                PreparedStatement psVentas = conn.prepareStatement(sqlVentas);
                psVentas.setString(1, fechaHoy);
                ResultSet rsVentas = psVentas.executeQuery();
                String ventasDiaTemp = "0";
                if (rsVentas.next()) {
                    ventasDiaTemp = String.format("%.2f €", rsVentas.getDouble(1));
                }
                rsVentas.close();
                psVentas.close();

                // Clientes activos (cuenta total de clientes)
                String sqlClientes = "SELECT COUNT(*) FROM Clientes";
                PreparedStatement psClientes = conn.prepareStatement(sqlClientes);
                ResultSet rsClientes = psClientes.executeQuery();
                String clientesActivosTemp = "0";
                if (rsClientes.next()) {
                    clientesActivosTemp = String.valueOf(rsClientes.getInt(1));
                }
                rsClientes.close();
                psClientes.close();

                // Productos en stock (sumar StockActual)
                String sqlProductos = "SELECT COALESCE(SUM(StockActual),0) FROM Productos";
                PreparedStatement psProductos = conn.prepareStatement(sqlProductos);
                ResultSet rsProductos = psProductos.executeQuery();
                String productosStockTemp = "0";
                if (rsProductos.next()) {
                    productosStockTemp = String.valueOf(rsProductos.getInt(1));
                }
                rsProductos.close();
                psProductos.close();

                // Alertas de inventario (productos con StockActual <= StockMinimo)
                String sqlAlertas = "SELECT COUNT(*) FROM Productos WHERE StockActual <= StockMinimo";
                PreparedStatement psAlertas = conn.prepareStatement(sqlAlertas);
                ResultSet rsAlertas = psAlertas.executeQuery();
                String alertasTemp = "0";
                if (rsAlertas.next()) {
                    alertasTemp = String.valueOf(rsAlertas.getInt(1));
                }
                rsAlertas.close();
                psAlertas.close();

                conn.close();

                final String ventasDia = ventasDiaTemp;
                final String clientesActivos = clientesActivosTemp;
                final String productosStock = productosStockTemp;
                final String alertas = alertasTemp;

                // Actualizar UI en el hilo Swing
                SwingUtilities.invokeLater(() -> {
                    ventasLabel.setText(ventasDia);
                    clientesLabel.setText(clientesActivos);
                    productosLabel.setText(productosStock);
                    alertasLabel.setText(alertas);
                });

            } catch (SQLException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    ventasLabel.setText("Error");
                    clientesLabel.setText("Error");
                    productosLabel.setText("Error");
                    alertasLabel.setText("Error");
                });
            }
        }).start();
    }
}


