/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: NavigationPanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NavigationPanel extends JPanel {
    private MainFrame parent;
    
    public NavigationPanel(MainFrame parent) {
        this.parent = parent;
        setupNavigationPanel();
    }
    
    private void setupNavigationPanel() {
        setPreferredSize(new Dimension(200, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Botones de navegación
        JButton[] navButtons = {
            createNavButton("Dashboard", "home"),
            createNavButton("Inventario", "inventory"),
            createNavButton("Ventas", "sales"),
            createNavButton("Clientes", "customers"),
            createNavButton("Proveedores", "suppliers"),
            createNavButton("Empleados", "employees"),
            createNavButton("Informes", "reports"),
            createNavButton("Tarifas", "tariffs"),
            createNavButton("Configuración", "settings")
        };
        
        for (JButton button : navButtons) {
            add(button);
            add(Box.createVerticalStrut(5));
        }
        
        // Añadir espacio flexible al final
        add(Box.createVerticalGlue());
    }
    
    private JButton createNavButton(String text, String command) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setActionCommand(command);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(new NavigationActionListener(parent));
        return button;
    }
}
