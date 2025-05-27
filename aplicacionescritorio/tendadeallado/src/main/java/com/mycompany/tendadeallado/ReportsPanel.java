/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReportsPanel extends JPanel {
    private JLabel statusBar;
    
    public ReportsPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        initializePanel();
    }

    
    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Informes y Estadísticas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel de selección de informes
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Tipo de Informe:"));
        JComboBox<String> reportType = new JComboBox<>(new String[]{
            "Ventas por Período", 
            "Ventas por Producto", 
            "Ventas por Cliente", 
            "Inventario Actual", 
            "Productos de Baja Rotación",
            "Rendimiento de Empleados"
        });
        selectionPanel.add(reportType);
        
        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodPanel.add(new JLabel("Período:"));
        periodPanel.add(new JComboBox<>(new String[]{
            "Hoy", 
            "Esta Semana", 
            "Este Mes", 
            "Este Trimestre",
            "Este Año",
            "Personalizado..."
        }));
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Desde:"));
        datePanel.add(new JTextField(10));
        datePanel.add(new JLabel("Hasta:"));
        datePanel.add(new JTextField(10));
        datePanel.add(new JButton("Aplicar"));
        
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(titleLabel);
        topPanel.add(selectionPanel);
        JPanel dateSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateSelectionPanel.add(periodPanel);
        dateSelectionPanel.add(datePanel);
        topPanel.add(dateSelectionPanel);
        
        // Panel para gráficos
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Simulación de un gráfico (en una aplicación real, aquí iría un componente de gráficos)
        JPanel dummyChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                
                int width = getWidth();
                int height = getHeight();
                
                // Fondo
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, width, height);
                
                // Ejes
                g2.setColor(Color.BLACK);
                g2.drawLine(50, height - 50, width - 50, height - 50); // eje X
                g2.drawLine(50, 50, 50, height - 50); // eje Y
                
                // Etiquetas en eje X
                String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun"};
                int xAxisLength = width - 100;
                int step = xAxisLength / 6;
                
                for (int i = 0; i < 6; i++) {
                    g2.drawString(months[i], 50 + i * step, height - 30);
                }
                
                // Etiquetas en eje Y
                for (int i = 0; i < 5; i++) {
                    g2.drawString(String.valueOf((4 - i) * 25) + "K", 30, 50 + i * (height - 100) / 4);
                }
                
                // Barras del gráfico
                int[] values = {65, 45, 80, 30, 95, 60};
                int maxHeight = height - 100;
                
                for (int i = 0; i < 6; i++) {
                    int barHeight = values[i] * maxHeight / 100;
                    g2.setColor(new Color(70, 130, 180, 200));
                    g2.fillRect(60 + i * step, height - 50 - barHeight, step - 20, barHeight);
                    g2.setColor(new Color(30, 70, 130));
                    g2.drawRect(60 + i * step, height - 50 - barHeight, step - 20, barHeight);
                }
                
                // Título del gráfico
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("Ventas Mensuales (en miles $)", width / 2 - 100, 30);
            }
        };
        
        chartPanel.add(dummyChartPanel, BorderLayout.CENTER);
        
        // Panel de resumen de datos
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        
        // Tabla de resumen
        String[] summaryColumns = {"Período", "Ventas Totales", "Productos Vendidos", "Ticket Promedio", "Margen"};
        Object[][] summaryData = {
            {"Enero", "$65,432", "532", "$123.00", "32%"},
            {"Febrero", "$45,678", "412", "$110.87", "29%"},
            {"Marzo", "$80,123", "687", "$116.63", "35%"},
            {"Abril", "$30,456", "289", "$105.38", "27%"},
            {"Mayo", "$95,789", "823", "$116.39", "38%"},
            {"Junio", "$60,234", "513", "$117.42", "33%"}
        };
        
        JTable summaryTable = new JTable(summaryData, summaryColumns);
        JScrollPane summaryScroll = new JScrollPane(summaryTable);
        
        summaryPanel.add(summaryScroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("Exportar a Excel"));
        buttonPanel.add(new JButton("Exportar a PDF"));
        buttonPanel.add(new JButton("Imprimir"));
        
        // Añadir componentes al panel principal
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(chartPanel, BorderLayout.CENTER);
        centerPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Actualizar barra de estado
        if (statusBar != null) {
            statusBar.setText(" Informes - Visualizando informe de ventas por período");
        }
    }
}
