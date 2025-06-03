package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;

public class PanelManager {
    private MainFrame parent;
    private HomePanel homePanel;
    private InventoryPanel inventoryPanel;
    private SalesPanel salesPanel;
    private CustomersPanel customersPanel;
    private EmployeesPanel employeesPanel;
    private ReportsPanel reportsPanel;
    private CustomizationPanel customizationPanel;
    private ThemeManager themeManager;
    
    public PanelManager(MainFrame parent) {
        this.parent = parent;
        // Inicializar el ThemeManager
        this.themeManager = new ThemeManager();
        initializePanels();
    }
    
    private void initializePanels() {
    homePanel = new HomePanel();
    inventoryPanel = new InventoryPanel(parent.getStatusBar());
    salesPanel = new SalesPanel(parent.getStatusBar());
    customersPanel = new CustomersPanel(parent.getStatusBar());
    employeesPanel = new EmployeesPanel(parent.getStatusBar());
    reportsPanel = new ReportsPanel(parent.getStatusBar());
    
    customizationPanel = null;  // se crea dinámicamente
}

    //Visualización de los JPaneles
    public void showHomePanel(JPanel contentPanel) {
    switchPanel(contentPanel, homePanel);
}

public void showInventoryPanel(JPanel contentPanel) {
    switchPanel(contentPanel, inventoryPanel);
}

public void showSalesPanel(JPanel contentPanel) {
    switchPanel(contentPanel, salesPanel);
}

public void showCustomersPanel(JPanel contentPanel) {
    switchPanel(contentPanel, customersPanel);
}

public void showEmployeesPanel (JPanel contentPanel){
    switchPanel(contentPanel, employeesPanel);
}

public void showReportsPanel(JPanel contentPanel) {
    switchPanel(contentPanel, reportsPanel);
}

    
    public void showCustomizationPanel(JPanel contentPanel, ThemeManager themeManager) {
        // Usar el ThemeManager pasado como parámetro o el interno
        ThemeManager activeThemeManager = (themeManager != null) ? themeManager : this.themeManager;
        
        // Crear una nueva instancia del CustomizationPanel con los parámetros correctos
        customizationPanel = new CustomizationPanel(contentPanel, activeThemeManager, parent);
        
        switchPanel(contentPanel, customizationPanel);
    }
    
    // Método sobrecargado para usar el ThemeManager interno
    public void showCustomizationPanel(JPanel contentPanel) {
        showCustomizationPanel(contentPanel, this.themeManager);
    }
    
    private void switchPanel(JPanel contentPanel, JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // Métodos getter para acceder a los componentes
    public ThemeManager getThemeManager() {
        return themeManager;
    }
    
    public MainFrame getMainFrame() {
        return parent;
    }
    
    // Método para aplicar tema a todos los paneles
    public void applyThemeToAllPanels() {
        if (themeManager != null) {
            // Aplicar tema al frame principal
            themeManager.applyThemeToComponent(parent);
            
            // Aplicar tema a los paneles individuales
            if (homePanel != null) {
                themeManager.applyThemeToComponent(homePanel);
            }
            if (inventoryPanel != null) {
                themeManager.applyThemeToComponent(inventoryPanel);
            }
            if (salesPanel != null) {
                themeManager.applyThemeToComponent(salesPanel);
            }
            if (customersPanel != null) {
                themeManager.applyThemeToComponent(customersPanel);
            }
            if (employeesPanel != null){
                themeManager.applyThemeToComponent(employeesPanel);
            }
            if (reportsPanel != null) {
                themeManager.applyThemeToComponent(reportsPanel);
            }
            if (customizationPanel != null) {
                themeManager.applyThemeToComponent(customizationPanel);
            }
        }
    }
    
    // Método para refrescar el panel de personalización
    public void refreshCustomizationPanel(JPanel contentPanel) {
        if (customizationPanel != null) {
            showCustomizationPanel(contentPanel);
        }
    }
}