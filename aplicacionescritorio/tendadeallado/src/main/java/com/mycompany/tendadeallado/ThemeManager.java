/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: ThemeManager.java
import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    private Color[] themeColors = {
        new Color(240, 240, 240), // Claro (por defecto)
        new Color(50, 50, 50),    // Oscuro
        new Color(220, 235, 250), // Azul claro
        new Color(250, 235, 215)  // Beige
    };
    
    private Color currentThemeColor = themeColors[0];
    
    public void applyTheme(int themeIndex, JFrame parent) {
        if (themeIndex >= 0 && themeIndex < themeColors.length) {
            currentThemeColor = themeColors[themeIndex];
            
            JOptionPane.showMessageDialog(
                parent,
                "Tema aplicado: " + getThemeName(themeIndex),
                "Cambio de Tema",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private String getThemeName(int index) {
        String[] names = {"Claro", "Oscuro", "Azul", "Beige"};
        return index < names.length ? names[index] : "Desconocido";
    }
    
    public Color getCurrentThemeColor() {
        return currentThemeColor;
    }
    
    public Color[] getThemeColors() {
        return themeColors.clone();
    }
    
    public JPanel createThemePreviewPanel(String themeName, Color themeColor, boolean isSelected) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            isSelected ? BorderFactory.createLineBorder(Color.BLUE, 2) : BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel nameLabel = new JLabel(themeName);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel de vista previa del tema
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Fondo
                g.setColor(themeColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Dibujar simulación de elementos de interfaz
                Color textColor = themeColor.equals(themeColors[1]) ? Color.WHITE : Color.BLACK;
                
                // Barra superior
                g.setColor(themeColor.darker());
                g.fillRect(0, 0, getWidth(), 20);
                
                // Panel izquierdo
                g.fillRect(0, 20, 40, getHeight() - 20);
                
                // Botones en panel izquierdo
                g.setColor(themeColor.brighter());
                for (int i = 0; i < 5; i++) {
                    g.fillRect(5, 30 + i * 25, 30, 20);
                }
                
                // Contenido principal
                g.setColor(textColor);
                for (int i = 0; i < 4; i++) {
                    g.drawLine(50, 40 + i * 20, getWidth() - 10, 40 + i * 20);
                }
            }
        };
        
        previewPanel.setPreferredSize(new Dimension(150, 100));
        
        // Panel inferior con botón de selección
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JRadioButton selectButton = new JRadioButton("Seleccionar");
        selectButton.setSelected(isSelected);
        bottomPanel.add(selectButton);
        
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(previewPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}
