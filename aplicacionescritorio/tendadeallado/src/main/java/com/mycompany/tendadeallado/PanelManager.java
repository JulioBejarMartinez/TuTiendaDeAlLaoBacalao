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
    private ProvidersPanel providersPanel;
    private ReportsPanel reportsPanel;
    private PriceCalculatorPanel priceCalculatorPanel;
    private CustomizationPanel customizationPanel;
    private ThemeManager themeManager;
    private ConfigReader configReader;
    
    public PanelManager(MainFrame parent) {
        this.parent = parent;
        // Inicializar el ThemeManager
        this.themeManager = new ThemeManager();
        this.configReader = new ConfigReader("config.xml");
        DatabaseHelper.setConfigReader(configReader);

        initializePanels();
    }
    
    private void initializePanels() {
    homePanel = new HomePanel(configReader);
    inventoryPanel = new InventoryPanel(parent.getStatusBar(), configReader);
    salesPanel = new SalesPanel(parent.getStatusBar(), configReader);
    customersPanel = new CustomersPanel(parent.getStatusBar(), configReader);
    employeesPanel = new EmployeesPanel(parent.getStatusBar(), configReader);
    providersPanel = new ProvidersPanel(parent.getStatusBar(), configReader);
    priceCalculatorPanel = new PriceCalculatorPanel(parent.getStatusBar());
    reportsPanel = new ReportsPanel(parent.getStatusBar(), configReader);
    
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

public void showEmployeesPanel (JPanel contentPanel) {
    switchPanel(contentPanel, employeesPanel);
}
public void showProvidersPanel (JPanel contentPanel) {
    switchPanel(contentPanel, providersPanel);
}

public void showPriceCalculatorPanel (JPanel contentPanel) {
    switchPanel (contentPanel, priceCalculatorPanel);
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
            if (priceCalculatorPanel != null){
                themeManager.applyThemeToComponent(priceCalculatorPanel);
            }
            if (providersPanel != null){
                themeManager.applyThemeToComponent(providersPanel);
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