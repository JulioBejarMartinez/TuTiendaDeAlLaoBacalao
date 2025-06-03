/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: ToolBarActionListener.java
import javax.swing.*;
import java.awt.event.*;

public class ToolBarActionListener implements ActionListener {
    private MainFrame parent;
    
    public ToolBarActionListener(MainFrame parent) {
        this.parent = parent;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch(cmd) {
            case "home":
                parent.showHomePanel();
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
                
            case "employees":
                parent.showEmployeesPanel();
                break;
                
            case "reports":
                parent.showReportsPanel();
                break;
                
            case "settings":
                parent.showCustomizationPanel();
                break;
                
            case "logout":
                int option = JOptionPane.showConfirmDialog(
                    parent,
                    "¿Está seguro que desea cerrar sesión?",
                    "Confirmar Cierre de Sesión",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    parent.dispose();
                    // Asumiendo que existe una clase LoginFrame
                    new LoginFrame().setVisible(true);
                }
                break;
                
            default:
                JOptionPane.showMessageDialog(
                    parent,
                    "Función no implementada: " + cmd,
                    "En Desarrollo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                break;
        }
    }
}

