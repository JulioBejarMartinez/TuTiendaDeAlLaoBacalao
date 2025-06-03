/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: NavigationActionListener.java
import javax.swing.*;
import java.awt.event.*;

public class NavigationActionListener implements ActionListener {
    private MainFrame parent;
    
    public NavigationActionListener(MainFrame parent) {
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