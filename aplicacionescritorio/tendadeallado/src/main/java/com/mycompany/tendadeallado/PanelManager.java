package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;

public class PanelManager {
    private MainFrame parent;
    private HomePanel homePanel;
    private InventoryPanel inventoryPanel;
    private SalesPanel salesPanel;
    private CustomersPanel customersPanel;
    private ReportsPanel reportsPanel;
    private CustomizationPanel customizationPanel;

    public PanelManager(MainFrame parent) {
        this.parent = parent;
        initializePanels();
    }

    private void initializePanels() {
        homePanel = new HomePanel();
        inventoryPanel = new InventoryPanel(parent.getStatusBar());
        salesPanel = new SalesPanel(parent.getStatusBar());
        customersPanel = new CustomersPanel(parent.getStatusBar());
        reportsPanel = new ReportsPanel(parent.getStatusBar());
        customizationPanel = new CustomizationPanel();
    }

    public void showHomePanel(JPanel contentPanel) {
        switchPanel(contentPanel, homePanel.createPanel());
    }

    public void showInventoryPanel(JPanel contentPanel) {
        switchPanel(contentPanel, inventoryPanel); // Sin createPanel()
    }

    public void showSalesPanel(JPanel contentPanel) {
        switchPanel(contentPanel, salesPanel);
    }

    public void showCustomersPanel(JPanel contentPanel) {
        switchPanel(contentPanel, customersPanel);
    }

    public void showReportsPanel(JPanel contentPanel) {
        switchPanel(contentPanel, reportsPanel);
    }

    public void showCustomizationPanel(JPanel contentPanel, ThemeManager themeManager) {
        switchPanel(contentPanel, customizationPanel);
    }

    private void switchPanel(JPanel contentPanel, JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
