package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private Color[] themeColors = {
        new Color(240, 240, 240), // Claro (por defecto)
        new Color(50, 50, 50),    // Oscuro
        new Color(220, 235, 250), // Azul claro
        new Color(250, 235, 215)  // Beige
    };
    
    private String[] themeNames = {
        "Claro", "Oscuro", "Azul", "Beige"
    };
    
    private int currentThemeIndex = 0;
    private Color currentThemeColor = themeColors[0];
    private List<Component> registeredComponents = new ArrayList<>();
    
    public ThemeManager() {
        // Constructor por defecto
    }
    
    // Getters
    public Color[] getThemeColors() {
        return themeColors.clone();
    }
    
    public String[] getThemeNames() {
        return themeNames.clone();
    }
    
    public Color getCurrentThemeColor() {
        return currentThemeColor;
    }
    
    public int getCurrentThemeIndex() {
        return currentThemeIndex;
    }
    
    public String getCurrentThemeName() {
        return themeNames[currentThemeIndex];
    }
    
    // Aplicar tema por índice
    public void applyTheme(int themeIndex, JFrame parentFrame) {
        if (themeIndex >= 0 && themeIndex < themeColors.length) {
            currentThemeIndex = themeIndex;
            currentThemeColor = themeColors[themeIndex];
            
            // Aplicar tema al frame principal
            if (parentFrame != null) {
                applyThemeToComponent(parentFrame);
                parentFrame.repaint();
            }
            
            // Aplicar tema a todos los componentes registrados
            applyThemeToRegisteredComponents();
        }
    }
    public void setCurrentThemeColor(Color color) {
    this.currentThemeColor = color;
}


    // Aplicar tema por color
    public void applyTheme(Color themeColor, JFrame parentFrame) {
        for (int i = 0; i < themeColors.length; i++) {
            if (themeColors[i].equals(themeColor)) {
                applyTheme(i, parentFrame);
                break;
            }
        }
    }
    
    // Aplicar tema a un componente específico
    public void applyThemeToComponent(Component component) {
        if (component == null) return;
        
        Color backgroundColor = currentThemeColor;
        Color foregroundColor = getForegroundColor();
        
        // Aplicar colores al componente
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);
        
        // Si es un contenedor, aplicar recursivamente a sus hijos
        if (component instanceof Container) {
            Container container = (Container) component;
            applyThemeToContainer(container, backgroundColor, foregroundColor);
        }
    }
    
    // Aplicar tema a un contenedor y sus componentes hijos
    private void applyThemeToContainer(Container container, Color backgroundColor, Color foregroundColor) {
        container.setBackground(backgroundColor);
        container.setForeground(foregroundColor);
        
        for (Component child : container.getComponents()) {
            if (child instanceof JButton) {
                applyThemeToButton((JButton) child, backgroundColor, foregroundColor);
            } else if (child instanceof JLabel) {
                applyThemeToLabel((JLabel) child, foregroundColor);
            } else if (child instanceof JTextField) {
                applyThemeToTextField((JTextField) child, backgroundColor, foregroundColor);
            } else if (child instanceof JTextArea) {
                applyThemeToTextArea((JTextArea) child, backgroundColor, foregroundColor);
            } else if (child instanceof JTable) {
                applyThemeToTable((JTable) child, backgroundColor, foregroundColor);
            } else if (child instanceof JTabbedPane) {
                applyThemeToTabbedPane((JTabbedPane) child, backgroundColor, foregroundColor);
            } else if (child instanceof JPanel) {
                applyThemeToPanel((JPanel) child, backgroundColor, foregroundColor);
            } else {
                child.setBackground(backgroundColor);
                child.setForeground(foregroundColor);
            }
            
            // Aplicar recursivamente si es un contenedor
            if (child instanceof Container) {
                applyThemeToContainer((Container) child, backgroundColor, foregroundColor);
            }
        }
    }
    
    // Métodos específicos para diferentes tipos de componentes
    private void applyThemeToButton(JButton button, Color backgroundColor, Color foregroundColor) {
        button.setBackground(backgroundColor.brighter());
        button.setForeground(foregroundColor);
    }
    
    private void applyThemeToLabel(JLabel label, Color foregroundColor) {
        label.setForeground(foregroundColor);
    }
    
    private void applyThemeToTextField(JTextField textField, Color backgroundColor, Color foregroundColor) {
        textField.setBackground(backgroundColor.brighter());
        textField.setForeground(foregroundColor);
        textField.setCaretColor(foregroundColor);
    }
    
    private void applyThemeToTextArea(JTextArea textArea, Color backgroundColor, Color foregroundColor) {
        textArea.setBackground(backgroundColor.brighter());
        textArea.setForeground(foregroundColor);
        textArea.setCaretColor(foregroundColor);
    }
    
    private void applyThemeToTable(JTable table, Color backgroundColor, Color foregroundColor) {
        table.setBackground(backgroundColor.brighter());
        table.setForeground(foregroundColor);
        table.setGridColor(foregroundColor.brighter());
        
        // Aplicar tema al header de la tabla
        if (table.getTableHeader() != null) {
            table.getTableHeader().setBackground(backgroundColor.darker());
            table.getTableHeader().setForeground(foregroundColor);
        }
    }
    
    private void applyThemeToTabbedPane(JTabbedPane tabbedPane, Color backgroundColor, Color foregroundColor) {
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setForeground(foregroundColor);
    }
    
    private void applyThemeToPanel(JPanel panel, Color backgroundColor, Color foregroundColor) {
        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);
    }
    
    // Registrar componente para aplicación automática de temas
    public void registerComponent(Component component) {
        if (component != null && !registeredComponents.contains(component)) {
            registeredComponents.add(component);
            applyThemeToComponent(component);
        }
    }
    
    // Desregistrar componente
    public void unregisterComponent(Component component) {
        registeredComponents.remove(component);
    }
    
    // Aplicar tema a todos los componentes registrados
    private void applyThemeToRegisteredComponents() {
        for (Component component : registeredComponents) {
            if (component != null) {
                applyThemeToComponent(component);
            }
        }
    }
    
    // Obtener color de primer plano apropiado según el tema
    public Color getForegroundColor() {
        // Para el tema oscuro, usar texto blanco
        if (currentThemeIndex == 1) { // Tema oscuro
            return Color.WHITE;
        }
        // Para otros temas, usar texto negro
        return Color.BLACK;
    }
    
    // Método para obtener color de acento
    public Color getAccentColor() {
        switch (currentThemeIndex) {
            case 0: return new Color(0, 120, 215);    // Azul para tema claro
            case 1: return new Color(100, 200, 255);  // Azul claro para tema oscuro
            case 2: return new Color(0, 78, 156);     // Azul oscuro para tema azul
            case 3: return new Color(139, 69, 19);    // Marrón para tema beige
            default: return new Color(0, 120, 215);
        }
    }
    // Agregar método para obtener el índice del tema por color
    public int getThemeIndexByColor(Color color) {
        for (int i = 0; i < themeColors.length; i++) {
            if (themeColors[i].equals(color)) {
                return i;
                }
            }
            return 0; // Retorna tema por defecto si no encuentra coincidencia
        }
    // Agregar método para sincronizar el índice del tema actual
    public void syncThemeIndex() {
        for (int i = 0; i < themeColors.length; i++) {
            if (themeColors[i].equals(currentThemeColor)) {
                currentThemeIndex = i;
                break;
            }
        }
    }
    // Restablecer tema por defecto
    public void resetToDefault(JFrame parentFrame) {
        applyTheme(0, parentFrame);
    }
    
    // Método para limpiar componentes registrados (útil para liberación de memoria)
    public void clearRegisteredComponents() {
        registeredComponents.clear();
    }
}