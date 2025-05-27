package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;

public class SalesPanel extends JPanel {
    private JLabel statusBar;

    public SalesPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        // No inicializar aquí, lo haremos en createPanel()
    }

    public JPanel createPanel() {
        initializePanel();
        return this;
    }

    private void initializePanel() {
        removeAll(); // Limpio por si se llama varias veces

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Gestión de Ventas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.add(new JButton("Nueva Venta"));
        optionsPanel.add(new JButton("Ver Historial"));
        optionsPanel.add(new JButton("Devoluciones"));
        
        topPanel.add(optionsPanel, BorderLayout.CENTER);
        
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel productsPanel = new JPanel(new BorderLayout(0, 10));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Productos"));
        
        JPanel searchProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchProductPanel.add(new JLabel("Buscar producto:"));
        searchProductPanel.add(new JTextField(15));
        searchProductPanel.add(new JButton("Buscar"));
        
        String[] productColumns = {"ID", "Nombre", "Precio", "Stock"};
        Object[][] productData = {
            {"001", "Laptop HP 15", "$899.99", 15},
            {"002", "Smartphone Samsung", "$499.99", 23},
            {"003", "Camiseta Algodón", "$19.99", 50},
            {"004", "Pantalón Vaquero", "$39.99", 30},
            {"005", "Arroz 1Kg", "$2.99", 100},
            {"006", "Aceite de Oliva", "$9.99", 40}
        };
        
        JTable productTable = new JTable(productData, productColumns);
        JScrollPane productScroll = new JScrollPane(productTable);
        
        productsPanel.add(searchProductPanel, BorderLayout.NORTH);
        productsPanel.add(productScroll, BorderLayout.CENTER);
        
        JPanel cartPanel = new JPanel(new BorderLayout(0, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Carrito de Compra"));
        
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.add(new JLabel("Cliente:"));
        clientPanel.add(new JComboBox<>(new String[]{"Cliente Ocasional", "Juan Pérez", "María López", "Carlos Rodríguez"}));
        
        String[] cartColumns = {"Producto", "Cantidad", "Precio Unit.", "Subtotal", ""};
        Object[][] cartData = {
            {"Laptop HP 15", 1, "$899.99", "$899.99", "X"},
            {"Smartphone Samsung", 2, "$499.99", "$999.98", "X"}
        };
        
        JTable cartTable = new JTable(cartData, cartColumns);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        
        JPanel totalsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        totalsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        totalsPanel.add(new JLabel("Subtotal:"));
        totalsPanel.add(new JLabel("$1,899.97"));
        
        totalsPanel.add(new JLabel("IVA (16%):"));
        totalsPanel.add(new JLabel("$304.00"));
        
        totalsPanel.add(new JLabel("Total:"));
        JLabel totalLabel = new JLabel("$2,203.97");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalsPanel.add(totalLabel);
        
        totalsPanel.add(new JLabel("Método de Pago:"));
        totalsPanel.add(new JComboBox<>(new String[]{"Efectivo", "Tarjeta de Crédito", "Transferencia"}));
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(new JButton("Cancelar Venta"));
        actionPanel.add(new JButton("Procesar Pago"));
        
        cartPanel.add(clientPanel, BorderLayout.NORTH);
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        
        JPanel southCartPanel = new JPanel(new BorderLayout());
        southCartPanel.add(totalsPanel, BorderLayout.CENTER);
        southCartPanel.add(actionPanel, BorderLayout.SOUTH);
        
        cartPanel.add(southCartPanel, BorderLayout.SOUTH);
        
        mainContent.add(productsPanel);
        mainContent.add(cartPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        
        if (statusBar != null) {
            statusBar.setText(" Gestión de Ventas - Nueva venta en progreso");
        }
    }
}
