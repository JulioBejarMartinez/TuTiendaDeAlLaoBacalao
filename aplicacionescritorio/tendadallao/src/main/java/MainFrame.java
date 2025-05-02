/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PRACTICAS
 */
// Archivo: MainFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class MainFrame extends JFrame {
    // Componentes principales
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JLabel statusBar;
    private String username;
    
    // Temas predefinidos
    private Color[] themeColors = {
        new Color(240, 240, 240), // Claro (por defecto)
        new Color(50, 50, 50),    // Oscuro
        new Color(220, 235, 250), // Azul claro
        new Color(250, 235, 215)  // Beige
    };
    
    private Color currentThemeColor = themeColors[0];
    
    public MainFrame(String username) {
        this.username = username;
        
        // Configuración de la ventana
        setTitle("Sistema de Gestión de Tienda - Panel Principal");
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Inicializar componentes
        setupMenuBar();
        setupToolBar();
        setupMainPanel();
        setupStatusBar();
        
        // Configurar layout principal
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(toolBar, BorderLayout.NORTH);
        container.add(mainPanel, BorderLayout.CENTER);
        container.add(statusBar, BorderLayout.SOUTH);
        
        // Mostrar panel de inicio por defecto
        showHomePanel();
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
        
        setJMenuBar(menuBar);
    }
    
    private JMenuItem createMenuItem(String text, String command, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setActionCommand(command);
        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }
        menuItem.addActionListener(new MenuActionListener());
        return menuItem;
    }
    
    private void setupToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Botones de la barra de herramientas
        toolBar.add(createToolButton("Inicio", "home", "Ir a la pantalla de inicio"));
        toolBar.add(createToolButton("Inventario", "inventory", "Gestión de inventario"));
        toolBar.add(createToolButton("Ventas", "sales", "Gestión de ventas"));
        toolBar.add(createToolButton("Clientes", "customers", "Gestión de clientes"));
        toolBar.add(createToolButton("Informes", "reports", "Informes y estadísticas"));
        
        toolBar.addSeparator();
        toolBar.add(createToolButton("Configuración", "settings", "Configuración del sistema"));
        
        // Añadir espacio flexible para separar botones de logout
        toolBar.add(Box.createHorizontalGlue());
        
        JLabel userLabel = new JLabel("Usuario: " + username + " ");
        toolBar.add(userLabel);
        toolBar.add(createToolButton("Cerrar Sesión", "logout", "Cerrar sesión"));
    }
    
    private JButton createToolButton(String text, String command, String tooltip) {
        JButton button = new JButton(text);
        button.setActionCommand(command);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.addActionListener(new ToolBarActionListener());
        return button;
    }
    
    private void setupMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        
        // Panel de navegación izquierdo
        JPanel navPanel = new JPanel();
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Botones de navegación
        JButton[] navButtons = {
            createNavButton("Dashboard", "home"),
            createNavButton("Inventario", "inventory"),
            createNavButton("Ventas", "sales"),
            createNavButton("Clientes", "customers"),
            createNavButton("Proveedores", "suppliers"),
            createNavButton("Empleados", "employees"),
            createNavButton("Informes", "reports"),
            createNavButton("Configuración", "settings")
        };
        
        for (JButton button : navButtons) {
            navPanel.add(button);
            navPanel.add(Box.createVerticalStrut(5));
        }
        
        // Añadir espacio flexible al final
        navPanel.add(Box.createVerticalGlue());
        
        // Panel de contenido (centro)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Añadir paneles al panel principal
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private JButton createNavButton(String text, String command) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setActionCommand(command);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(new NavigationActionListener());
        return button;
    }
    
    private void setupStatusBar() {
        statusBar = new JLabel(" Sistema listo. Usuario: " + username);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    // Métodos para mostrar diferentes paneles
    private void showHomePanel() {
        contentPanel.removeAll();
        
        JPanel homePanel = new JPanel(new BorderLayout(10, 10));
        homePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Dashboard - Resumen del Sistema");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        // Tarjetas de estadísticas
        statsPanel.add(createStatsCard("Ventas del día", "$1,256.50", "↑ 12%"));
        statsPanel.add(createStatsCard("Clientes activos", "128", "↑ 5%"));
        statsPanel.add(createStatsCard("Productos en stock", "1,546", ""));
        statsPanel.add(createStatsCard("Alertas de inventario", "8", "⚠"));
        
        // Panel de acciones rápidas
        JPanel quickActionsPanel = new JPanel();
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder("Acciones Rápidas"));
        quickActionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        quickActionsPanel.add(createQuickActionButton("Nueva Venta"));
        quickActionsPanel.add(createQuickActionButton("Agregar Producto"));
        quickActionsPanel.add(createQuickActionButton("Registrar Cliente"));
        quickActionsPanel.add(createQuickActionButton("Generar Informe"));
        
        // Añadir componentes al panel principal
        homePanel.add(titleLabel, BorderLayout.NORTH);
        homePanel.add(statsPanel, BorderLayout.CENTER);
        homePanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        statusBar.setText(" Inicio - Resumen del sistema");
    }
    
    private JPanel createStatsCard(String title, String value, String change) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel changeLabel = new JLabel(change);
        if (change.contains("↑")) {
            changeLabel.setForeground(new Color(0, 150, 0));
        } else if (change.contains("↓")) {
            changeLabel.setForeground(new Color(150, 0, 0));
        }
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(changeLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JButton createQuickActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        return button;
    }
    
    private void showInventoryPanel() {
        contentPanel.removeAll();
        
        JPanel inventoryPanel = new JPanel(new BorderLayout(10, 10));
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Gestión de Inventario");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel de búsqueda y filtros
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(new JTextField(20));
        searchPanel.add(new JButton("Buscar"));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Categoría:"));
        filterPanel.add(new JComboBox<>(new String[]{"Todas", "Electrónica", "Ropa", "Alimentos", "Hogar"}));
        filterPanel.add(new JLabel("Ordenar por:"));
        filterPanel.add(new JComboBox<>(new String[]{"Nombre", "Precio", "Stock", "Categoría"}));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        // Tabla de productos
        String[] columnNames = {"ID", "Nombre", "Categoría", "Precio", "Stock", "Acciones"};
        Object[][] data = {
            {"001", "Laptop HP 15", "Electrónica", "$899.99", 15, ""},
            {"002", "Smartphone Samsung", "Electrónica", "$499.99", 23, ""},
            {"003", "Camiseta Algodón", "Ropa", "$19.99", 50, ""},
            {"004", "Pantalón Vaquero", "Ropa", "$39.99", 30, ""},
            {"005", "Arroz 1Kg", "Alimentos", "$2.99", 100, ""},
            {"006", "Aceite de Oliva", "Alimentos", "$9.99", 40, ""},
            {"007", "Lámpara de Mesa", "Hogar", "$29.99", 12, ""},
            {"008", "Juego de Sábanas", "Hogar", "$49.99", 8, ""}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JButton("Nuevo Producto"));
        buttonPanel.add(new JButton("Editar Seleccionado"));
        buttonPanel.add(new JButton("Eliminar"));
        buttonPanel.add(new JButton("Importar"));
        buttonPanel.add(new JButton("Exportar"));
        
        // Añadir componentes al panel principal
        inventoryPanel.add(topPanel, BorderLayout.NORTH);
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);
        inventoryPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(inventoryPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        statusBar.setText(" Gestión de Inventario - 8 productos en la lista");
    }
    
    private void showSalesPanel() {
        contentPanel.removeAll();
        
        JPanel salesPanel = new JPanel(new BorderLayout(10, 10));
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Gestión de Ventas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel superior con opciones
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.add(new JButton("Nueva Venta"));
        optionsPanel.add(new JButton("Ver Historial"));
        optionsPanel.add(new JButton("Devoluciones"));
        
        topPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Panel principal dividido en dos partes
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Panel izquierdo: Productos y búsqueda
        JPanel productsPanel = new JPanel(new BorderLayout(0, 10));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Productos"));
        
        JPanel searchProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchProductPanel.add(new JLabel("Buscar producto:"));
        searchProductPanel.add(new JTextField(15));
        searchProductPanel.add(new JButton("Buscar"));
        
        // Lista de productos con scroll
        String[] productColumns = {"ID", "Nombre", "Precio", "Stock"};
        Object[][] productData = {
            {"001", "Laptop HP 15", "$899.99", 15},
            {"002", "Smartphone Samsung", "$499.99", 23},
            {"003", "Camiseta Algodón", "$19.99", 50},
            {"004", "Pantalón Vaquero", "$39.99", 30},
            {"005", "Arroz 1Kg", "$2.99", 100},
            {"006", "Aceite de Oliva", "$9.99", 40}
        };
        
        JTable productTable = new JTable(productData, productColumns);
        JScrollPane productScroll = new JScrollPane(productTable);
        
        productsPanel.add(searchProductPanel, BorderLayout.NORTH);
        productsPanel.add(productScroll, BorderLayout.CENTER);
        
        // Panel derecho: Carrito de compra actual
        JPanel cartPanel = new JPanel(new BorderLayout(0, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Carrito de Compra"));
        
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.add(new JLabel("Cliente:"));
        clientPanel.add(new JComboBox<>(new String[]{"Cliente Ocasional", "Juan Pérez", "María López", "Carlos Rodríguez"}));
        
        // Tabla del carrito
        String[] cartColumns = {"Producto", "Cantidad", "Precio Unit.", "Subtotal", ""};
        Object[][] cartData = {
            {"Laptop HP 15", 1, "$899.99", "$899.99", "X"},
            {"Smartphone Samsung", 2, "$499.99", "$999.98", "X"}
        };
        
        JTable cartTable = new JTable(cartData, cartColumns);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        
        // Panel de totales y pago
        JPanel totalsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        totalsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        totalsPanel.add(new JLabel("Subtotal:"));
        totalsPanel.add(new JLabel("$1,899.97"));
        
        totalsPanel.add(new JLabel("IVA (16%):"));
        totalsPanel.add(new JLabel("$304.00"));
        
        totalsPanel.add(new JLabel("Total:"));
        JLabel totalLabel = new JLabel("$2,203.97");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalsPanel.add(totalLabel);
        
        totalsPanel.add(new JLabel("Método de Pago:"));
        totalsPanel.add(new JComboBox<>(new String[]{"Efectivo", "Tarjeta de Crédito", "Transferencia"}));
        
        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(new JButton("Cancelar Venta"));
        actionPanel.add(new JButton("Procesar Pago"));
        
        // Añadir componentes al panel del carrito
        cartPanel.add(clientPanel, BorderLayout.NORTH);
        cartPanel.add(cartScroll, BorderLayout.CENTER);
        
        JPanel southCartPanel = new JPanel(new BorderLayout());
        southCartPanel.add(totalsPanel, BorderLayout.CENTER);
        southCartPanel.add(actionPanel, BorderLayout.SOUTH);
        
        cartPanel.add(southCartPanel, BorderLayout.SOUTH);
        
        // Añadir paneles al contenido principal
        mainContent.add(productsPanel);
        mainContent.add(cartPanel);
        
        // Añadir todo al panel de ventas
        salesPanel.add(topPanel, BorderLayout.NORTH);
        salesPanel.add(mainContent, BorderLayout.CENTER);
        
        contentPanel.add(salesPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        statusBar.setText(" Gestión de Ventas - Nueva venta en progreso");
    }
    
    private void showCustomersPanel() {
        contentPanel.removeAll();
        
        JPanel customersPanel = new JPanel(new BorderLayout(10, 10));
        customersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Gestión de Clientes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(new JTextField(20));
        searchPanel.add(new JButton("Buscar"));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Tabla de clientes
        String[] columnNames = {"ID", "Nombre", "Email", "Teléfono", "Dirección", "Acciones"};
        Object[][] data = {
            {"C001", "Juan Pérez", "juan@email.com", "555-123-4567", "Calle Principal 123", ""},
            {"C002", "María López", "maria@email.com", "555-234-5678", "Avenida Central 456", ""},
            {"C003", "Carlos Rodríguez", "carlos@email.com", "555-345-6789", "Plaza Mayor 789", ""},
            {"C004", "Ana Martínez", "ana@email.com", "555-456-7890", "Calle Secundaria 101", ""},
            {"C005", "Pedro Sánchez", "pedro@email.com", "555-567-8901", "Avenida Norte 202", ""}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Panel de detalles del cliente (mostrado al seleccionar un cliente)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Cliente"));
        
        JPanel clientInfoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        clientInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        clientInfoPanel.add(new JLabel("ID:"));
        clientInfoPanel.add(new JTextField("C001"));
        
        clientInfoPanel.add(new JLabel("Nombre:"));
        clientInfoPanel.add(new JTextField("Juan Pérez"));
        
        clientInfoPanel.add(new JLabel("Email:"));
        clientInfoPanel.add(new JTextField("juan@email.com"));
        
        clientInfoPanel.add(new JLabel("Teléfono:"));
        clientInfoPanel.add(new JTextField("555-123-4567"));
        
        clientInfoPanel.add(new JLabel("Dirección:"));
        clientInfoPanel.add(new JTextField("Calle Principal 123"));
        
        JPanel clientButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clientButtonsPanel.add(new JButton("Guardar Cambios"));
        clientButtonsPanel.add(new JButton("Historial de Compras"));
        
        detailsPanel.add(clientInfoPanel, BorderLayout.CENTER);
        detailsPanel.add(clientButtonsPanel, BorderLayout.SOUTH);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JButton("Nuevo Cliente"));
        buttonPanel.add(new JButton("Editar Seleccionado"));
        buttonPanel.add(new JButton("Eliminar"));
        buttonPanel.add(new JButton("Importar"));
        buttonPanel.add(new JButton("Exportar"));
        
        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, detailsPanel);
        splitPane.setResizeWeight(0.6); // 60% arriba, 40% abajo
        
        // Añadir componentes al panel principal
        customersPanel.add(topPanel, BorderLayout.NORTH);
        customersPanel.add(splitPane, BorderLayout.CENTER);
        customersPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(customersPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        statusBar.setText(" Gestión de Clientes - 5 clientes en la lista");
    }
    
    private void showReportsPanel() {
        contentPanel.removeAll();
        
        JPanel reportsPanel = new JPanel(new BorderLayout(10, 10));
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Informes y Estadísticas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel de selección de informes
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Tipo de Informe:"));
        JComboBox<String> reportType = new JComboBox<>(new String[]{
            "Ventas por Período", 
            "Ventas por Producto", 
            "Ventas por Cliente", 
            "Inventario Actual", 
            "Productos de Baja Rotación",
            "Rendimiento de Empleados"
        });
        selectionPanel.add(reportType);
        
        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        periodPanel.add(new JLabel("Período:"));
        periodPanel.add(new JComboBox<>(new String[]{
            "Hoy", 
            "Esta Semana", 
            "Este Mes", 
            "Este Trimestre",
            "Este Año",
            "Personalizado..."
        }));
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Desde:"));
        datePanel.add(new JTextField(10));
        datePanel.add(new JLabel("Hasta:"));
        datePanel.add(new JTextField(10));
        datePanel.add(new JButton("Aplicar"));
        
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(titleLabel);
        topPanel.add(selectionPanel);
        JPanel dateSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateSelectionPanel.add(periodPanel);
        dateSelectionPanel.add(datePanel);
        topPanel.add(dateSelectionPanel);
        
        // Panel para gráficos
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Simulación de un gráfico (en una aplicación real, aquí iría un componente de gráficos)
        JPanel dummyChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                
                int width = getWidth();
                int height = getHeight();
                
                // Fondo
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, width, height);
                
                // Ejes
                g2.setColor(Color.BLACK);
                g2.drawLine(50, height - 50, width - 50, height - 50); // eje X
                g2.drawLine(50, 50, 50, height - 50); // eje Y
                
                // Etiquetas en eje X
                String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun"};
                int xAxisLength = width - 100;
                int step = xAxisLength / 6;
                
                for (int i = 0; i < 6; i++) {
                    g2.drawString(months[i], 50 + i * step, height - 30);
                }
                
                // Etiquetas en eje Y
                for (int i = 0; i < 5; i++) {
                    g2.drawString(String.valueOf((4 - i) * 25) + "K", 30, 50 + i * (height - 100) / 4);
                }
                
                // Barras del gráfico
                int[] values = {65, 45, 80, 30, 95, 60};
                int maxHeight = height - 100;
                
                for (int i = 0; i < 6; i++) {
                    int barHeight = values[i] * maxHeight / 100;
                    g2.setColor(new Color(70, 130, 180, 200));
                    g2.fillRect(60 + i * step, height - 50 - barHeight, step - 20, barHeight);
                    g2.setColor(new Color(30, 70, 130));
                    g2.drawRect(60 + i * step, height - 50 - barHeight, step - 20, barHeight);
                }
                
                // Título del gráfico
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("Ventas Mensuales (en miles $)", width / 2 - 100, 30);
            }
        };
        
        chartPanel.add(dummyChartPanel, BorderLayout.CENTER);
        
        // Panel de resumen de datos
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        
        // Tabla de resumen
        String[] summaryColumns = {"Período", "Ventas Totales", "Productos Vendidos", "Ticket Promedio", "Margen"};
        Object[][] summaryData = {
            {"Enero", "$65,432", "532", "$123.00", "32%"},
            {"Febrero", "$45,678", "412", "$110.87", "29%"},
            {"Marzo", "$80,123", "687", "$116.63", "35%"},
            {"Abril", "$30,456", "289", "$105.38", "27%"},
            {"Mayo", "$95,789", "823", "$116.39", "38%"},
            {"Junio", "$60,234", "513", "$117.42", "33%"}
        };
        
        JTable summaryTable = new JTable(summaryData, summaryColumns);
        JScrollPane summaryScroll = new JScrollPane(summaryTable);
        
        summaryPanel.add(summaryScroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("Exportar a Excel"));
        buttonPanel.add(new JButton("Exportar a PDF"));
        buttonPanel.add(new JButton("Imprimir"));
        
        // Añadir componentes al panel principal
        reportsPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(chartPanel, BorderLayout.CENTER);
        centerPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        reportsPanel.add(centerPanel, BorderLayout.CENTER);
        reportsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(reportsPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        statusBar.setText(" Informes - Visualizando informe de ventas por período");
    }
    
    private void showCustomizationPanel() {
        contentPanel.removeAll();
        
        JPanel customizePanel = new JPanel(new BorderLayout(10, 10));
        customizePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Personalización de Interfaz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña de Temas
        JPanel themesPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel themesGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        themesGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Paneles de previsualización de temas
        themesGrid.add(createThemePreviewPanel("Tema Claro", themeColors[0], true));
        themesGrid.add(createThemePreviewPanel("Tema Oscuro", themeColors[1], false));
        themesGrid.add(createThemePreviewPanel("Tema Azul", themeColors[2], false));
        themesGrid.add(createThemePreviewPanel("Tema Beige", themeColors[3], false));
        
        themesPanel.add(new JLabel("Seleccione un tema:"), BorderLayout.NORTH);
        themesPanel.add(themesGrid, BorderLayout.CENTER);
        
        // Pestaña de Módulos
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
        
        upButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        downButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        orderButtonPanel.add(Box.createVerticalGlue());
        orderButtonPanel.add(upButton);
        orderButtonPanel.add(Box.createVerticalStrut(10));
        orderButtonPanel.add(downButton);
        orderButtonPanel.add(Box.createVerticalGlue());
        
        moduleOrderPanel.add(orderScroll, BorderLayout.CENTER);
        moduleOrderPanel.add(orderButtonPanel, BorderLayout.EAST);
        
        modulesPanel.add(moduleOrderPanel, BorderLayout.CENTER);
        
        // Pestaña de Apariencia
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
        
        // Añadir pestañas al panel
        tabbedPane.addTab("Temas", themesPanel);
        tabbedPane.addTab("Módulos", modulesPanel);
        tabbedPane.addTab("Apariencia", appearancePanel);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("Restaurar Valores Predeterminados"));
        buttonPanel.add(new JButton("Cancelar"));
        buttonPanel.add(new JButton("Aplicar"));
        buttonPanel.add(new JButton("Guardar"));
        
        // Añadir componentes al panel principal
        customizePanel.add(titleLabel, BorderLayout.NORTH);
        customizePanel.add(tabbedPane, BorderLayout.CENTER);
        customizePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(customizePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        statusBar.setText(" Personalización - Configurando la interfaz del sistema");
    }
    
    private JPanel createThemePreviewPanel(String themeName, Color themeColor, boolean isSelected) {
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
                g.setColor(textColor);
                
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
    
    // Clases para el manejo de eventos
    
    class MenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            
            switch(cmd) {
                case "exit":
                    int option = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "¿Está seguro que desea salir del sistema?",
                        "Confirmar Salida",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                    break;
                    
                case "inventory":
                    showInventoryPanel();
                    break;
                    
                case "sales":
                    showSalesPanel();
                    break;
                    
                case "customers":
                    showCustomersPanel();
                    break;
                    
                case "reports":
                    showReportsPanel();
                    break;
                    
                case "theme_light":
                    applyTheme(0);
                    break;
                    
                case "theme_dark":
                    applyTheme(1);
                    break;
                    
                case "theme_blue":
                    applyTheme(2);
                    break;
                    
                case "theme_beige":
                    applyTheme(3);
                    break;
                    
                case "customize_ui":
                    showCustomizationPanel();
                    break;
                    
                case "about":
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Sistema de Gestión de Tienda v1.0\n\n" +
                        "Desarrollado por Luis E. Martínez y Julio Bejar\n" +
                        "Todos los derechos reservados © 2025",
                        "Acerca de",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
                    
                default:
                    // Para funciones no implementadas
                    if (!cmd.startsWith("theme_")) {
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            "Función no implementada: " + cmd,
                            "En Desarrollo",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    break;
            }
        }
    }
    
    class ToolBarActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            
            switch(cmd) {
                case "home":
                    showHomePanel();
                    break;
                    
                case "inventory":
                    showInventoryPanel();
                    break;
                    
                case "sales":
                    showSalesPanel();
                    break;
                    
                case "customers":
                    showCustomersPanel();
                    break;
                    
                case "reports":
                    showReportsPanel();
                    break;
                    
                case "settings":
                    showCustomizationPanel();
                    break;
                    
                case "logout":
                    int option = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "¿Está seguro que desea cerrar sesión?",
                        "Confirmar Cierre de Sesión",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        dispose();
                        new LoginFrame().setVisible(true);
                    }
                    break;
                    
                default:
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Función no implementada: " + cmd,
                        "En Desarrollo",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
            }
        }
    }
    
    class NavigationActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            
            switch(cmd) {
                case "home":
                    showHomePanel();
                    break;
                    
                case "inventory":
                    showInventoryPanel();
                    break;
                    
                case "sales":
                    showSalesPanel();
                    break;
                    
                case "customers":
                    showCustomersPanel();
                    break;
                    
                case "reports":
                    showReportsPanel();
                    break;
                    
                case "settings":
                    showCustomizationPanel();
                    break;
                    
                default:
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Función no implementada: " + cmd,
                        "En Desarrollo",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
            }
        }
    }
    
    private void applyTheme(int themeIndex) {
        if (themeIndex >= 0 && themeIndex < themeColors.length) {
            currentThemeColor = themeColors[themeIndex];
            
            // Aquí se implementaría el cambio real de temas en la aplicación
            // Este es un ejemplo simplificado
            
            JOptionPane.showMessageDialog(
                this,
                "Tema aplicado: " + 
                (themeIndex == 0 ? "Claro" : 
                 themeIndex == 1 ? "Oscuro" : 
                 themeIndex == 2 ? "Azul" : "Beige"),
                "Cambio de Tema",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
        
        
