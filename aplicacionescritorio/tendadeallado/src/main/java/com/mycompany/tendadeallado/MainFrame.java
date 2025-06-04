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

    private MenuBarManager menuBarManager;
    private ToolBarManager toolBarManager;
    private NavigationPanel navigationPanel;
    private ThemeManager themeManager;

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

    public void showHomePanel() {
        JPanel panel = new HomePanel();
        updateStatusBar("Inicio - Resumen del sistema");
        if (themeManager != null) {
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }

    public void showInventoryPanel() {
        JPanel panel = new InventoryPanel(getStatusBar());
        updateStatusBar("Gestión de Inventario");
        if (themeManager != null) {
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }

    public void showSalesPanel() {
        JPanel panel = new SalesPanel(getStatusBar());
        updateStatusBar("Gestión de Ventas");
        if (themeManager != null) {
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }

    public void showCustomersPanel() {
        JPanel panel = new CustomersPanel(getStatusBar());
        updateStatusBar("Gestión de Clientes");
        if (themeManager != null) {
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }
    
    public void showEmployeesPanel() {
        JPanel panel = new EmployeesPanel(getStatusBar());
        updateStatusBar("Gestión de Empleados");
        if (themeManager != null){
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }
    
    public void showProvidersPanel() {
        JPanel panel = new ProvidersPanel (getStatusBar());
        updateStatusBar("Gestión de Proveedores");
        if (themeManager != null){
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }

    public void showReportsPanel() {
        JPanel panel = new ReportsPanel(getStatusBar());
        updateStatusBar("Informes y Estadísticas");
        if (themeManager != null) {
            themeManager.applyThemeToComponent(panel);
        }
        setContentPanel(panel);
    }
    public void showPriceCalculatorPanel(){
    PriceCalculatorPanel priceCalculator = new PriceCalculatorPanel(getStatusBar());
    JPanel panel = priceCalculator.createPanel();
    updateStatusBar("Calculadora de Tarifas");
    if (themeManager != null) {
        themeManager.applyThemeToComponent(panel);
    }
    setContentPanel(panel);
}

    public void showCustomizationPanel() {
    JPanel panel = new CustomizationPanel(contentPanel, themeManager, this);
    updateStatusBar("Personalización");
    if (themeManager != null) {
        themeManager.applyThemeToComponent(panel);
    }
    setContentPanel(panel);
}


    private void setContentPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void updateStatusBar(String message) {
    statusBar.setText(" " + message);
    if (themeManager.getCurrentThemeIndex() == 1) { // Tema oscuro
        statusBar.setForeground(Color.WHITE);
        statusBar.setBackground(themeManager.getCurrentThemeColor());
    } else {
        statusBar.setForeground(Color.BLACK);
        statusBar.setBackground(null);
    }
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

    public static int getCurrentEmployeeId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentEmployeeId'");
    }

}
