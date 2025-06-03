package com.mycompany.tendadeallado;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PRACTICAS
 */
// Archivo: HomePanel.java
import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    
    public HomePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Dashboard - Resumen del Sistema");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        // Tarjetas de estadísticas
        statsPanel.add(createStatsCard("Ventas del día", "$1,256.50", "↑ 12%"));
        statsPanel.add(createStatsCard("Clientes activos", "128", "↑ 5%"));
        statsPanel.add(createStatsCard("Productos en stock", "1,546", ""));
        statsPanel.add(createStatsCard("Alertas de inventario", "8", "⚠"));
        
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
    }
    
    private JPanel createStatsCard(String title, String value, String change) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value);
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
}
