/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
// Archivo: MenuBarManager.java
import javax.swing.*;
import java.awt.event.*;

public class MenuBarManager {
    private JMenuBar menuBar;
    private MainFrame parent;
    private ThemeManager themeManager;
    
    public MenuBarManager(MainFrame parent, ThemeManager themeManager) {
        this.parent = parent;
        this.themeManager = themeManager;
        setupMenuBar();
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.add(createMenuItem("Nuevo", "new", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)));
        fileMenu.add(createMenuItem("Abrir", "open", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Salir", "exit", KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK)));
        
        // Menú Editar
        JMenu editMenu = new JMenu("Editar");
        editMenu.add(createMenuItem("Cortar", "cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)));
        editMenu.add(createMenuItem("Copiar", "copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)));
        editMenu.add(createMenuItem("Pegar", "paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)));
        
        // Menú Ver
        JMenu viewMenu = new JMenu("Ver");
        viewMenu.add(createMenuItem("Inventario", "inventory", null));
        viewMenu.add(createMenuItem("Ventas", "sales", null));
        viewMenu.add(createMenuItem("Clientes", "customers", null));
        viewMenu.add(createMenuItem("Informes", "reports", null));
        
        // Menú Personalización
        JMenu customizeMenu = new JMenu("Personalizar");
        JMenu themesMenu = new JMenu("Temas");
        themesMenu.add(createMenuItem("Claro", "theme_light", null));
        themesMenu.add(createMenuItem("Oscuro", "theme_dark", null));
        themesMenu.add(createMenuItem("Azul", "theme_blue", null));
        themesMenu.add(createMenuItem("Beige", "theme_beige", null));
        customizeMenu.add(themesMenu);
        customizeMenu.add(createMenuItem("Configurar Interfaz", "customize_ui", null));
        
        // Menú Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.add(createMenuItem("Manual de Usuario", "user_manual", null));
        helpMenu.add(createMenuItem("Acerca de", "about", null));
        
        // Añadir menús a la barra de menú
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(customizeMenu);
        menuBar.add(helpMenu);
    }
    
    private JMenuItem createMenuItem(String text, String command, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setActionCommand(command);
        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }
        menuItem.addActionListener(new MenuActionListener(parent, themeManager));
        return menuItem;
    }
    
    public JMenuBar getMenuBar() {
        return menuBar;
    }
}

