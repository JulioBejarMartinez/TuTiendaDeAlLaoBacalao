package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;

public class CustomizationPanel extends JPanel {
    // Temas predefinidos
    private Color[] themeColors = {
        new Color(240, 240, 240), // Claro (por defecto)
        new Color(50, 50, 50),    // Oscuro
        new Color(220, 235, 250), // Azul claro
        new Color(250, 235, 215)  // Beige
    };
    
    private Color currentThemeColor = themeColors[0];
    private JPanel parentPanel;
    
    // Constructor principal
    public CustomizationPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        initializePanel();
    }

    // Constructor sin parámetros, opcional, inicializa sin parentPanel
    public CustomizationPanel() {
        this.parentPanel = null;
        initializePanel();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Personalización de Interfaz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Temas", createThemesPanel());
        tabbedPane.addTab("Módulos", createModulesPanel());
        tabbedPane.addTab("Apariencia", createAppearancePanel());
        
        add(titleLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    // --- El resto de métodos se mantienen igual ---
    private JPanel createThemesPanel() {
        JPanel themesPanel = new JPanel(new BorderLayout(10, 10));
        JPanel themesGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        themesGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        themesGrid.add(createThemePreviewPanel("Tema Claro", themeColors[0], currentThemeColor.equals(themeColors[0])));
        themesGrid.add(createThemePreviewPanel("Tema Oscuro", themeColors[1], currentThemeColor.equals(themeColors[1])));
        themesGrid.add(createThemePreviewPanel("Tema Azul", themeColors[2], currentThemeColor.equals(themeColors[2])));
        themesGrid.add(createThemePreviewPanel("Tema Beige", themeColors[3], currentThemeColor.equals(themeColors[3])));
        
        themesPanel.add(new JLabel("Seleccione un tema:"), BorderLayout.NORTH);
        themesPanel.add(themesGrid, BorderLayout.CENTER);
        
        return themesPanel;
    }
    
    private JPanel createModulesPanel() {
        JPanel modulesPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel modulesListPanel = new JPanel();
        modulesListPanel.setLayout(new BoxLayout(modulesListPanel, BoxLayout.Y_AXIS));
        
        JCheckBox[] moduleCheckboxes = {
            new JCheckBox("Dashboard", true),
            new JCheckBox("Inventario", true),
            new JCheckBox("Ventas", true),
            new JCheckBox("Clientes", true),
            new JCheckBox("Proveedores", true),
            new JCheckBox("Empleados", true),
            new JCheckBox("Informes", true),
            new JCheckBox("Configuración", true)
        };
        
        JPanel moduleOptions = new JPanel(new BorderLayout());
        moduleOptions.setBorder(BorderFactory.createTitledBorder("Módulos Visibles"));
        
        for (JCheckBox checkbox : moduleCheckboxes) {
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            modulesListPanel.add(checkbox);
            modulesListPanel.add(Box.createVerticalStrut(5));
        }
        
        moduleOptions.add(modulesListPanel, BorderLayout.CENTER);
        modulesPanel.add(moduleOptions, BorderLayout.NORTH);
        modulesPanel.add(createModuleOrderPanel(), BorderLayout.CENTER);
        
        return modulesPanel;
    }
    
    private JPanel createModuleOrderPanel() {
        JPanel moduleOrderPanel = new JPanel(new BorderLayout());
        moduleOrderPanel.setBorder(BorderFactory.createTitledBorder("Orden de los Módulos"));
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Dashboard");
        listModel.addElement("Inventario");
        listModel.addElement("Ventas");
        listModel.addElement("Clientes");
        listModel.addElement("Proveedores");
        listModel.addElement("Empleados");
        listModel.addElement("Informes");
        listModel.addElement("Configuración");
        
        JList<String> orderList = new JList<>(listModel);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane orderScroll = new JScrollPane(orderList);
        
        JPanel orderButtonPanel = new JPanel();
        orderButtonPanel.setLayout(new BoxLayout(orderButtonPanel, BoxLayout.Y_AXIS));
        JButton upButton = new JButton("▲ Subir");
        JButton downButton = new JButton("▼ Bajar");
        
        upButton.addActionListener(e -> moveListItem(orderList, listModel, -1));
        downButton.addActionListener(e -> moveListItem(orderList, listModel, 1));
        
        upButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        downButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        orderButtonPanel.add(Box.createVerticalGlue());
        orderButtonPanel.add(upButton);
        orderButtonPanel.add(Box.createVerticalStrut(10));
        orderButtonPanel.add(downButton);
        orderButtonPanel.add(Box.createVerticalGlue());
        
        moduleOrderPanel.add(orderScroll, BorderLayout.CENTER);
        moduleOrderPanel.add(orderButtonPanel, BorderLayout.EAST);
        
        return moduleOrderPanel;
    }
    
    private JPanel createAppearancePanel() {
        JPanel appearancePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        appearancePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        appearancePanel.add(new JLabel("Tamaño de Fuente:"));
        appearancePanel.add(new JComboBox<>(new String[]{"Pequeño", "Mediano", "Grande"}));
        
        appearancePanel.add(new JLabel("Familia de Fuente:"));
        appearancePanel.add(new JComboBox<>(new String[]{"Arial", "Times New Roman", "Calibri", "Segoe UI"}));
        
        appearancePanel.add(new JLabel("Modo de Iconos:"));
        appearancePanel.add(new JComboBox<>(new String[]{"Solo Iconos", "Solo Texto", "Iconos y Texto"}));
        
        appearancePanel.add(new JLabel("Densidad de Interfaz:"));
        appearancePanel.add(new JComboBox<>(new String[]{"Compacta", "Normal", "Espaciosa"}));
        
        appearancePanel.add(new JLabel("Animaciones:"));
        JCheckBox animationsCheckbox = new JCheckBox("Activar animaciones");
        appearancePanel.add(animationsCheckbox);
        
        return appearancePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton restoreButton = new JButton("Restaurar Valores Predeterminados");
        JButton cancelButton = new JButton("Cancelar");
        JButton applyButton = new JButton("Aplicar");
        JButton saveButton = new JButton("Guardar");
        
        restoreButton.addActionListener(e -> restoreDefaults());
        cancelButton.addActionListener(e -> cancelChanges());
        applyButton.addActionListener(e -> applyChanges());
        saveButton.addActionListener(e -> saveChanges());
        
        buttonPanel.add(restoreButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    private JPanel createThemePreviewPanel(String themeName, Color themeColor, boolean isSelected) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            isSelected ? BorderFactory.createLineBorder(Color.BLUE, 2) : BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel nameLabel = new JLabel(themeName);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(themeColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                Color textColor = themeColor.equals(themeColors[1]) ? Color.WHITE : Color.BLACK;
                g.setColor(textColor);
                
                g.setColor(themeColor.darker());
                g.fillRect(0, 0, getWidth(), 20);
                
                g.fillRect(0, 20, 40, getHeight() - 20);
                
                g.setColor(themeColor.brighter());
                for (int i = 0; i < 5; i++) {
                    g.fillRect(5, 30 + i * 25, 30, 20);
                }
                
                g.setColor(textColor);
                for (int i = 0; i < 4; i++) {
                    g.drawLine(50, 40 + i * 20, getWidth() - 10, 40 + i * 20);
                }
            }
        };
        
        previewPanel.setPreferredSize(new Dimension(150, 100));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JRadioButton selectButton = new JRadioButton("Seleccionar");
        selectButton.setSelected(isSelected);
        
        selectButton.addActionListener(e -> selectTheme(themeName, themeColor));
        
        bottomPanel.add(selectButton);
        
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(previewPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void moveListItem(JList<String> list, DefaultListModel<String> model, int direction) {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1) return;
        
        int newIndex = selectedIndex + direction;
        if (newIndex < 0 || newIndex >= model.getSize()) return;
        
        String selectedItem = model.get(selectedIndex);
        model.remove(selectedIndex);
        model.add(newIndex, selectedItem);
        list.setSelectedIndex(newIndex);
    }
    
    private void selectTheme(String themeName, Color themeColor) {
        currentThemeColor = themeColor;
        JOptionPane.showMessageDialog(
            this,
            "Tema seleccionado: " + themeName,
            "Selección de Tema",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void restoreDefaults() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea restaurar los valores predeterminados?",
            "Confirmar Restauración",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            currentThemeColor = themeColors[0];
            JOptionPane.showMessageDialog(
                this,
                "Valores predeterminados restaurados",
                "Restauración Completada",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void cancelChanges() {
        JOptionPane.showMessageDialog(
            this,
            "Cambios cancelados",
            "Cancelar",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void applyChanges() {
        JOptionPane.showMessageDialog(
            this,
            "Cambios aplicados",
            "Aplicar",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void saveChanges() {
        JOptionPane.showMessageDialog(
            this,
            "Cambios guardados",
            "Guardar",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
  
    public JPanel createPanel(ThemeManager themeManager) {
        return this;
    }
}
