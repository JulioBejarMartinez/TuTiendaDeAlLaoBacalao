/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: ToolBarManager.java
import java.awt.Component;
import javax.swing.*;
import java.awt.event.*;

public class ToolBarManager {
    private JToolBar toolBar;
    private MainFrame parent;
    private String username;
    
    public ToolBarManager(MainFrame parent, String username) {
        this.parent = parent;
        this.username = username;
        setupToolBar();
    }
    
    private void setupToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Botones de la barra de herramientas
        toolBar.add(createToolButton("Inicio", "home", "Ir a la pantalla de inicio"));
        toolBar.add(createToolButton("Inventario", "inventory", "Gestión de inventario"));
        toolBar.add(createToolButton("Ventas", "sales", "Gestión de ventas"));
        toolBar.add(createToolButton("Clientes", "customers", "Gestión de clientes"));
        toolBar.add(createToolButton("Empleados", "employees", "Gestión de empleados"));
        toolBar.add(createToolButton("Informes", "reports", "Informes y estadísticas"));
        
        toolBar.addSeparator();
        toolBar.add(createToolButton("Configuración", "settings", "Configuración del sistema"));
        
        // Añadir espacio flexible para separar botones de logout
        toolBar.add(Box.createHorizontalGlue());
        
        JLabel userLabel = new JLabel("Usuario: " + username + " ");
        toolBar.add(userLabel);
        toolBar.add(createToolButton("Cerrar Sesión", "logout", "Cerrar sesión"));
    }
    
    private JButton createToolButton(String text, String command, String tooltip) {
        JButton button = new JButton(text);
        button.setActionCommand(command);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.addActionListener(new ToolBarActionListener(parent));
        return button;
    }
    
    public JToolBar getToolBar() {
        return toolBar;
    }

}

