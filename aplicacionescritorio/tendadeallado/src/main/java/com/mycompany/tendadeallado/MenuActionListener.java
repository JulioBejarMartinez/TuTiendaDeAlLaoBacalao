/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: MenuActionListener.java
import javax.swing.*;
import java.awt.event.*;

public class MenuActionListener implements ActionListener {
    private MainFrame parent;
    private ThemeManager themeManager;
    
    public MenuActionListener(MainFrame parent, ThemeManager themeManager) {
        this.parent = parent;
        this.themeManager = themeManager;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch(cmd) {
            case "exit":
                int option = JOptionPane.showConfirmDialog(
                    parent,
                    "¿Está seguro que desea salir del sistema?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
                
            case "inventory":
                parent.showInventoryPanel();
                break;
                
            case "sales":
                parent.showSalesPanel();
                break;
                
            case "customers":
                parent.showCustomersPanel();
                break;
                
            case "reports":
                parent.showReportsPanel();
                break;
                
            case "theme_light":
                parent.applyTheme(0);
                break;
                
            case "theme_dark":
                parent.applyTheme(1);
                break;
                
            case "theme_blue":
                parent.applyTheme(2);
                break;
                
            case "theme_beige":
                parent.applyTheme(3);
                break;
                
            case "customize_ui":
                parent.showCustomizationPanel();
                break;
                
            case "about":
                JOptionPane.showMessageDialog(
                    parent,
                    "Sistema de Gestión de Tienda v1.0\n\n" +
                    "Desarrollado por Luis E. Martínez y Julio Bejar\n" +
                    "Todos los derechos reservados © 2025",
                    "Acerca de",
                    JOptionPane.INFORMATION_MESSAGE
                );
                break;
                
            default:
                // Para funciones no implementadas
                if (!cmd.startsWith("theme_")) {
                    JOptionPane.showMessageDialog(
                        parent,
                        "Función no implementada: " + cmd,
                        "En Desarrollo",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
                break;
        }
    }
}

