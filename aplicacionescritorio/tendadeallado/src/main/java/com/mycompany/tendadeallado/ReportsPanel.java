package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {
    private JLabel statusBar;
    private JTable summaryTable;
    private JComboBox<String> reportTypeCombo;
    private JPanel chartPanel;
    private ConfigReader configReader;

    public ReportsPanel(JLabel statusBar, ConfigReader configReader) {
        this.statusBar = statusBar;
       this.configReader = configReader;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Informes y Estadísticas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // ComboBox de tipos de informe
        reportTypeCombo = new JComboBox<>(new String[]{
            "Ventas por Período",
            "Ventas por Producto",
            "Ventas por Cliente",
            "Inventario Actual",
            "Productos de Baja Rotación"
        });
        reportTypeCombo.addActionListener(e -> actualizarInforme());

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Tipo de Informe:"));
        selectionPanel.add(reportTypeCombo);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(titleLabel);
        topPanel.add(selectionPanel);

        // Panel para el gráfico
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setPreferredSize(new Dimension(600, 200));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Gráfico"));

        // Panel para la tabla resumen
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        summaryTable = new JTable();
        summaryPanel.add(new JScrollPane(summaryTable), BorderLayout.CENTER);

        // Botones de exportación
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("Exportar a Excel"));
        buttonPanel.add(new JButton("Exportar a PDF"));
        buttonPanel.add(new JButton("Imprimir"));

        add(topPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);

        actualizarInforme();

        if (statusBar != null) {
            statusBar.setText(" Informes - Cargando informe inicial...");
        }
    }

    private void actualizarInforme() {
        String tipo = (String) reportTypeCombo.getSelectedItem();

        switch (tipo) {
            case "Ventas por Período":
                cargarVentasPorPeriodo();
                break;
            case "Ventas por Producto":
                cargarVentasPorProducto();
                break;
            case "Ventas por Cliente":
                cargarVentasPorCliente();
                break;
            case "Inventario Actual":
                cargarInventarioActual();
                break;
            case "Productos de Baja Rotación":
                cargarProductosBajaRotacion();
                break;
        }
    }

    private void cargarVentasPorPeriodo() {
        String[] columnas = {"Mes", "Ventas Totales", "Cantidad de Ventas", "Ticket Promedio"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT DATE_FORMAT(FechaHora, '%Y-%m') AS Mes, " +
                         "COUNT(*) AS NumVentas, SUM(Total) AS TotalVentas, AVG(Total) AS TicketPromedio " +
                         "FROM Ventas " +
                         "GROUP BY Mes ORDER BY Mes DESC LIMIT 6";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("Mes"),
                    String.format("$%.2f", rs.getDouble("TotalVentas")),
                    rs.getInt("NumVentas"),
                    String.format("$%.2f", rs.getDouble("TicketPromedio"))
                };
                model.addRow(fila);
            }

            summaryTable.setModel(model);
            pintarGraficoSimple(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void cargarVentasPorProducto() {
        String[] columnas = {"Producto", "Cantidad Vendida", "Total Generado"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = """
                SELECT p.Nombre, SUM(dv.Cantidad) AS CantidadVendida, SUM(dv.Cantidad * p.PrecioProducto) AS TotalGenerado
                FROM DetallesVenta dv
                JOIN Productos p ON p.ID_Producto = dv.ID_Producto
                GROUP BY p.ID_Producto
                ORDER BY CantidadVendida DESC LIMIT 10
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("Nombre"),
                    rs.getInt("CantidadVendida"),
                    String.format("$%.2f", rs.getDouble("TotalGenerado"))
                };
                model.addRow(fila);
            }

            summaryTable.setModel(model);
            pintarGraficoSimple(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage());
        }
    }

    private void cargarVentasPorCliente() {
        String[] columnas = {"Cliente", "Total Comprado", "Nº Compras"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = """
                SELECT c.Nombre, SUM(v.Total) AS TotalComprado, COUNT(*) AS NumCompras
                FROM Ventas v
                JOIN Clientes c ON c.ID_Cliente = v.ID_Cliente
                GROUP BY v.ID_Cliente
                ORDER BY TotalComprado DESC LIMIT 10
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("Nombre"),
                    String.format("$%.2f", rs.getDouble("TotalComprado")),
                    rs.getInt("NumCompras")
                };
                model.addRow(fila);
            }

            summaryTable.setModel(model);
            pintarGraficoSimple(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void cargarInventarioActual() {
        String[] columnas = {"Producto", "Stock Actual", "Stock Mínimo", "Precio"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT Nombre, StockActual, StockMinimo, PrecioProducto FROM Productos ORDER BY Nombre";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("Nombre"),
                    rs.getInt("StockActual"),
                    rs.getInt("StockMinimo"),
                    String.format("$%.2f", rs.getDouble("PrecioProducto"))
                };
                model.addRow(fila);
            }

            summaryTable.setModel(model);
            chartPanel.removeAll();
            chartPanel.add(new JLabel("Inventario actual cargado."), BorderLayout.CENTER);
            revalidate();
            repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void cargarProductosBajaRotacion() {
        String[] columnas = {"Producto", "Veces Vendido", "Stock Actual"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = """
                SELECT p.Nombre, IFNULL(SUM(dv.Cantidad), 0) AS VecesVendido, p.StockActual
                FROM Productos p
                LEFT JOIN DetallesVenta dv ON p.ID_Producto = dv.ID_Producto
                GROUP BY p.ID_Producto
                ORDER BY VecesVendido ASC LIMIT 10
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("Nombre"),
                    rs.getInt("VecesVendido"),
                    rs.getInt("StockActual")
                };
                model.addRow(fila);
            }

            summaryTable.setModel(model);
            pintarGraficoSimple(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void pintarGraficoSimple(DefaultTableModel model) {
        chartPanel.removeAll();
        JPanel dummy = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (model.getRowCount() == 0) return;
                Graphics2D g2 = (Graphics2D) g;
                int width = getWidth(), height = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, width, height);

                int barWidth = width / model.getRowCount();
                int max = 1;

                for (int i = 0; i < model.getRowCount(); i++) {
                    try {
                        String raw = model.getValueAt(i, 1).toString().replace("$", "").replace(",", "");
                        max = Math.max(max, (int) Double.parseDouble(raw));
                    } catch (Exception ignored) {}
                }

                for (int i = 0; i < model.getRowCount(); i++) {
                    try {
                        String label = model.getValueAt(i, 0).toString();
                        String raw = model.getValueAt(i, 1).toString().replace("$", "").replace(",", "");
                        int value = (int) Double.parseDouble(raw);
                        int barHeight = (int) ((value / (double) max) * (height - 60));

                        g2.setColor(new Color(70, 130, 180));
                        g2.fillRect(i * barWidth + 10, height - barHeight - 20, barWidth - 20, barHeight);
                        g2.setColor(Color.BLACK);
                        g2.drawString(label, i * barWidth + 10, height - 5);
                    } catch (Exception ignored) {}
                }
            }
        };
        chartPanel.add(dummy, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
