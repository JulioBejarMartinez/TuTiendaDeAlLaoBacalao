package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JLabel statusBar;
    private String username;

    // Componentes principales
    private MenuBarManager menuBarManager;
    private ToolBarManager toolBarManager;
    private NavigationPanel navigationPanel;
    private PanelManager panelManager;
    private ThemeManager themeManager;

    // Datos conexión MySQL (modifica estos valores)
    private static final String DB_HOST = "dam2.colexio-karbo.com";
    private static final String DB_PORT = "3333";
    private static final String DB_NAME = "proyecto_lumarsan_jbejar";
    private static final String DB_USER = "dam2";
    private static final String DB_PASSWORD = "Ka3b0134679";

    public MainFrame(String username) {
        this.username = username;
        initializeComponents();
        setupLayout();
        showHomePanel();

        // Probar conexión al iniciar (opcional)
        Connection conn = getDatabaseConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeComponents() {
        setTitle("Sistema de Gestión de Tienda - Panel Principal");
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        themeManager = new ThemeManager();
        menuBarManager = new MenuBarManager(this, themeManager);
        toolBarManager = new ToolBarManager(this, username);
        navigationPanel = new NavigationPanel(this);
        panelManager = new PanelManager(this);

        setupMainPanel();
        setupStatusBar();
    }

    private void setupMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(navigationPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void setupStatusBar() {
        statusBar = new JLabel(" Sistema listo. Usuario: " + username);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void setupLayout() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(toolBarManager.getToolBar(), BorderLayout.NORTH);
        container.add(mainPanel, BorderLayout.CENTER);
        container.add(statusBar, BorderLayout.SOUTH);

        setJMenuBar(menuBarManager.getMenuBar());
    }

    // Métodos públicos para cambiar paneles
    public void showHomePanel() {
        panelManager.showHomePanel(contentPanel);
        updateStatusBar("Inicio - Resumen del sistema");
    }

    public void showInventoryPanel() {
        panelManager.showInventoryPanel(contentPanel);
        updateStatusBar("Gestión de Inventario");
    }

    public void showSalesPanel() {
        panelManager.showSalesPanel(contentPanel);
        updateStatusBar("Gestión de Ventas");
    }

    public void showCustomersPanel() {
        panelManager.showCustomersPanel(contentPanel);
        updateStatusBar("Gestión de Clientes");
    }

    public void showReportsPanel() {
        panelManager.showReportsPanel(contentPanel);
        updateStatusBar("Informes");
    }

    public void showCustomizationPanel() {
        panelManager.showCustomizationPanel(contentPanel, themeManager);
        updateStatusBar("Personalización");
    }

    public void updateStatusBar(String message) {
        statusBar.setText(" " + message);
    }

    public void applyTheme(int themeIndex) {
        themeManager.applyTheme(themeIndex, this);
    }

    public String getUsername() {
        return username;
    }

    public JLabel getStatusBar() {
        return statusBar;
    }

    // Método para conexión a base de datos
    public static Connection getDatabaseConnection() {
        Connection conn = null;
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            System.out.println("Conexión a la base de datos exitosa.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos.");
            e.printStackTrace();
        }
        return conn;
    }
}
