package com.aplicacionescritorio;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class Main extends JFrame {
    
    // Componentes principales
    private JPanel panelPrincipal;
    private JTabbedPane panelPestanas;
    private JTable tablaMuebles;
    private DefaultTableModel modeloTabla;
    private JPanel panelFormulario;
    private JPanel panelBusqueda;
    private JPanel panelEstadisticas;
    
    // Componentes del formulario
    private JTextField campoId;
    private JTextField campoNombre;
    private JComboBox<String> comboTipo;
    private JTextField campoPrecio;
    private JSpinner spinnerCantidad;
    private JTextArea campoDescripcion;
    private JComboBox<String> comboMaterial;
    private JComboBox<String> comboColor;
    private JCheckBox checkDisponible;
    
    // Componentes de búsqueda
    private JTextField campoBusqueda;
    private JComboBox<String> comboCriterio;
    
    // Formato de moneda
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
    
    // Colores de la interfaz
    private Color colorPrimario = new Color(61, 90, 128);
    private Color colorSecundario = new Color(152, 193, 217);
    private Color colorClaro = new Color(224, 251, 252);
    private Color colorAccent = new Color(238, 108, 77);
    
    public Main() {
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
        cargarDatosEjemplo();
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Gestión de Muebles");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Icono de la aplicación (usaríamos un ícono real)
        // setIconImage(new ImageIcon("ruta/al/icono.png").getImage());
    }
    
    private void inicializarComponentes() {
        // Panel principal con BorderLayout
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPrincipal.setBackground(colorClaro);
        setContentPane(panelPrincipal);
        
        // Barra de herramientas
        JToolBar barraHerramientas = crearBarraHerramientas();
        panelPrincipal.add(barraHerramientas, BorderLayout.NORTH);
        
        // Panel de pestañas
        panelPestanas = new JTabbedPane();
        panelPestanas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelPestanas.setForeground(colorPrimario);
        panelPrincipal.add(panelPestanas, BorderLayout.CENTER);
        
        // Crear pestañas
        crearPanelInventario();
        crearPanelNuevoMueble();
        crearPanelBusqueda();
        crearPanelEstadisticas();
        
        // Barra de estado
        JPanel barraEstado = crearBarraEstado();
        panelPrincipal.add(barraEstado, BorderLayout.SOUTH);
    }
    
    private JToolBar crearBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        barra.setBackground(colorPrimario);
        barra.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Botones de la barra de herramientas
        JButton btnNuevo = crearBotonHerramienta("Nuevo", "Crear nuevo registro", "nuevo");
        JButton btnGuardar = crearBotonHerramienta("Guardar", "Guardar cambios", "guardar");
        JButton btnEliminar = crearBotonHerramienta("Eliminar", "Eliminar selección", "eliminar");
        JButton btnExportar = crearBotonHerramienta("Exportar", "Exportar datos", "exportar");
        JButton btnImprimir = crearBotonHerramienta("Imprimir", "Imprimir reporte", "imprimir");
        
        barra.add(btnNuevo);
        barra.add(btnGuardar);
        barra.add(btnEliminar);
        barra.addSeparator();
        barra.add(btnExportar);
        barra.add(btnImprimir);
        
        return barra;
    }
    
    private JButton crearBotonHerramienta(String texto, String tooltip, String accion) {
        JButton boton = new JButton(texto);
        boton.setToolTipText(tooltip);
        boton.setActionCommand(accion);
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorPrimario);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        
        return boton;
    }
    
    private void crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(colorClaro);
        
        // Modelo de tabla
        String[] columnas = {"ID", "Nombre", "Tipo", "Material", "Color", "Precio", "Stock", "Disponible"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID no editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) return Boolean.class; // Columna "Disponible"
                return String.class;
            }
        };
        
        // Tabla
        tablaMuebles = new JTable(modeloTabla);
        configurarTabla();
        
        // Panel de búsqueda rápida
        JPanel panelBusquedaRapida = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusquedaRapida.setBackground(colorClaro);
        
        JLabel lblBuscar = new JLabel("Búsqueda rápida:");
        JTextField campoBusquedaRapida = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        estilizarBoton(btnBuscar);
        
        panelBusquedaRapida.add(lblBuscar);
        panelBusquedaRapida.add(campoBusquedaRapida);
        panelBusquedaRapida.add(btnBuscar);
        
        panel.add(panelBusquedaRapida, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaMuebles), BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setBackground(colorClaro);
        
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnDetalles = new JButton("Ver Detalles");
        
        estilizarBoton(btnEditar);
        estilizarBoton(btnEliminar, colorAccent);
        estilizarBoton(btnDetalles);
        
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnEliminar);
        panelAcciones.add(btnDetalles);
        
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        panelPestanas.addTab("Inventario", new ImageIcon(), panel, "Gestionar inventario de muebles");
    }
    
    private void configurarTabla() {
        // Configurar apariencia de la tabla
        tablaMuebles.setRowHeight(30);
        tablaMuebles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaMuebles.setSelectionBackground(colorSecundario);
        tablaMuebles.setSelectionForeground(Color.BLACK);
        tablaMuebles.setShowGrid(true);
        tablaMuebles.setGridColor(new Color(230, 230, 230));
        
        // Configurar cabecera
        JTableHeader header = tablaMuebles.getTableHeader();
        header.setBackground(colorPrimario);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Ajustar ancho de columnas
        TableColumnModel columnModel = tablaMuebles.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(180); // Nombre
        columnModel.getColumn(2).setPreferredWidth(120); // Tipo
        columnModel.getColumn(3).setPreferredWidth(120); // Material
        columnModel.getColumn(4).setPreferredWidth(100); // Color
        columnModel.getColumn(5).setPreferredWidth(100); // Precio
        columnModel.getColumn(6).setPreferredWidth(80);  // Stock
        columnModel.getColumn(7).setPreferredWidth(100); // Disponible
        
        // Renderizador para precios
        columnModel.getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.RIGHT);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value != null) {
                    value = formatoMoneda.format(Double.parseDouble(value.toString()));
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
    }
    
    private void crearPanelNuevoMueble() {
        panelFormulario = new JPanel(new BorderLayout(10, 10));
        panelFormulario.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(colorClaro);
        
        // Título del formulario
        JLabel lblTitulo = new JLabel("Registrar Nuevo Mueble");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(colorPrimario);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        panelFormulario.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel del formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(colorClaro);
        formulario.setBorder(new CompoundBorder(
            new LineBorder(colorSecundario, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Primera fila
        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("ID:"), gbc);
        
        gbc.gridx = 1;
        campoId = new JTextField(10);
        campoId.setEditable(false);
        campoId.setText("AUTO");
        formulario.add(campoId, gbc);
        
        gbc.gridx = 2;
        formulario.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridwidth = 2;
        campoNombre = new JTextField(20);
        formulario.add(campoNombre, gbc);
        
        // Segunda fila
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        String[] tiposMuebles = {"Silla", "Mesa", "Sofá", "Estantería", "Cama", "Armario", "Escritorio", "Otro"};
        comboTipo = new JComboBox<>(tiposMuebles);
        formulario.add(comboTipo, gbc);
        
        gbc.gridx = 2;
        formulario.add(new JLabel("Material:"), gbc);
        
        gbc.gridx = 3;
        String[] materiales = {"Madera", "Metal", "Plástico", "Vidrio", "Tela", "Cuero", "Mixto"};
        comboMaterial = new JComboBox<>(materiales);
        formulario.add(comboMaterial, gbc);
        
        // Tercera fila
        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(new JLabel("Color:"), gbc);
        
        gbc.gridx = 1;
        String[] colores = {"Negro", "Blanco", "Marrón", "Beige", "Gris", "Azul", "Rojo", "Verde", "Otro"};
        comboColor = new JComboBox<>(colores);
        formulario.add(comboColor, gbc);
        
        gbc.gridx = 2;
        formulario.add(new JLabel("Precio (€):"), gbc);
        
        gbc.gridx = 3;
        campoPrecio = new JTextField(10);
        formulario.add(campoPrecio, gbc);
        
        // Cuarta fila
        gbc.gridx = 0;
        gbc.gridy = 3;
        formulario.add(new JLabel("Cantidad:"), gbc);
        
        gbc.gridx = 1;
        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel(1, 0, 1000, 1);
        spinnerCantidad = new JSpinner(modeloSpinner);
        formulario.add(spinnerCantidad, gbc);
        
        gbc.gridx = 2;
        formulario.add(new JLabel("Disponible:"), gbc);
        
        gbc.gridx = 3;
        checkDisponible = new JCheckBox();
        checkDisponible.setSelected(true);
        checkDisponible.setBackground(colorClaro);
        formulario.add(checkDisponible, gbc);
        
        // Quinta fila (descripción)
        gbc.gridx = 0;
        gbc.gridy = 4;
        formulario.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        campoDescripcion = new JTextArea(5, 30);
        campoDescripcion.setLineWrap(true);
        campoDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(campoDescripcion);
        scrollDescripcion.setBorder(new LineBorder(Color.LIGHT_GRAY));
        formulario.add(scrollDescripcion, gbc);
        
        // Resetear constraints
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Panel central para el formulario
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(colorClaro);
        panelCentral.add(formulario, BorderLayout.NORTH);
        
        // Añadir un panel para la carga de imágenes (simulado)
        JPanel panelImagenes = new JPanel();
        panelImagenes.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            "Imágenes del Producto",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        panelImagenes.setBackground(colorClaro);
        panelImagenes.setPreferredSize(new Dimension(600, 200));
        
        // Layout del panel de imágenes
        panelImagenes.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Simular áreas de imagen
        for (int i = 0; i < 3; i++) {
            JPanel panelImg = new JPanel();
            panelImg.setPreferredSize(new Dimension(150, 150));
            panelImg.setBackground(Color.WHITE);
            panelImg.setBorder(new LineBorder(Color.LIGHT_GRAY));
            JLabel lblImagen = new JLabel("Imagen " + (i+1));
            lblImagen.setHorizontalAlignment(JLabel.CENTER);
            panelImg.add(lblImagen);
            
            panelImagenes.add(panelImg);
        }
        
        JButton btnAgregarImagen = new JButton("+ Agregar Imagen");
        estilizarBoton(btnAgregarImagen);
        panelImagenes.add(btnAgregarImagen);
        
        panelCentral.add(panelImagenes, BorderLayout.CENTER);
        panelFormulario.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(colorClaro);
        
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGuardar = new JButton("Guardar");
        
        estilizarBoton(btnCancelar, new Color(180, 180, 180));
        estilizarBoton(btnLimpiar, colorSecundario);
        estilizarBoton(btnGuardar, colorPrimario);
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGuardar);
        
        panelFormulario.add(panelBotones, BorderLayout.SOUTH);
        
        panelPestanas.addTab("Nuevo Mueble", new ImageIcon(), panelFormulario, "Registrar nuevo mueble");
    }
    
    private void crearPanelBusqueda() {
        panelBusqueda = new JPanel(new BorderLayout(10, 10));
        panelBusqueda.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelBusqueda.setBackground(colorClaro);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            "Filtros de Búsqueda",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        panelFiltros.setBackground(colorClaro);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Criterio de búsqueda
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFiltros.add(new JLabel("Buscar por:"), gbc);
        
        gbc.gridx = 1;
        String[] criterios = {"Nombre", "Tipo", "Material", "Color", "Precio", "Disponibilidad"};
        comboCriterio = new JComboBox<>(criterios);
        panelFiltros.add(comboCriterio, gbc);
        
        gbc.gridx = 2;
        panelFiltros.add(new JLabel("Texto:"), gbc);
        
        gbc.gridx = 3;
        gbc.gridwidth = 2;
        campoBusqueda = new JTextField(20);
        panelFiltros.add(campoBusqueda, gbc);
        
        // Segunda fila
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFiltros.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> comboTipoBusqueda = new JComboBox<>(new String[]{"Todos", "Silla", "Mesa", "Sofá", "Estantería", "Cama", "Armario", "Escritorio"});
        panelFiltros.add(comboTipoBusqueda, gbc);
        
        gbc.gridx = 2;
        panelFiltros.add(new JLabel("Material:"), gbc);
        
        gbc.gridx = 3;
        JComboBox<String> comboMaterialBusqueda = new JComboBox<>(new String[]{"Todos", "Madera", "Metal", "Plástico", "Vidrio", "Tela", "Cuero", "Mixto"});
        panelFiltros.add(comboMaterialBusqueda, gbc);
        
        // Tercera fila
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFiltros.add(new JLabel("Rango de precio:"), gbc);
        
        gbc.gridx = 1;
        JPanel panelPrecio = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPrecio.setBackground(colorClaro);
        JTextField campoPrecioMin = new JTextField(5);
        JTextField campoPrecioMax = new JTextField(5);
        panelPrecio.add(new JLabel("De"));
        panelPrecio.add(campoPrecioMin);
        panelPrecio.add(new JLabel("a"));
        panelPrecio.add(campoPrecioMax);
        panelPrecio.add(new JLabel("€"));
        panelFiltros.add(panelPrecio, gbc);
        
        gbc.gridx = 2;
        panelFiltros.add(new JLabel("Disponible:"), gbc);
        
        gbc.gridx = 3;
        JComboBox<String> comboDisponibilidad = new JComboBox<>(new String[]{"Todos", "Disponible", "No disponible"});
        panelFiltros.add(comboDisponibilidad, gbc);
        
        // Botones
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        JPanel panelBotonesFiltros = new JPanel(new GridLayout(2, 1, 0, 10));
        panelBotonesFiltros.setBackground(colorClaro);
        
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar Filtros");
        
        estilizarBoton(btnBuscar, colorPrimario);
        estilizarBoton(btnLimpiar, colorSecundario);
        
        panelBotonesFiltros.add(btnBuscar);
        panelBotonesFiltros.add(btnLimpiar);
        
        panelFiltros.add(panelBotonesFiltros, gbc);
        
        // Resultados (tabla igual que en Inventario)
        DefaultTableModel modeloResultados = new DefaultTableModel(new String[]{
            "ID", "Nombre", "Tipo", "Material", "Color", "Precio", "Stock", "Disponible"
        }, 0);
        
        JTable tablaResultados = new JTable(modeloResultados);
        tablaResultados.setRowHeight(30);
        tablaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaResultados.setSelectionBackground(colorSecundario);
        
        JTableHeader headerResultados = tablaResultados.getTableHeader();
        headerResultados.setBackground(colorPrimario);
        headerResultados.setForeground(Color.WHITE);
        headerResultados.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            "Resultados de Búsqueda",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        panelResultados.setBackground(colorClaro);
        
        JLabel lblResultados = new JLabel("0 resultados encontrados");
        lblResultados.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panelResultados.add(lblResultados, BorderLayout.NORTH);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setBackground(colorClaro);
        
        JButton btnExportar = new JButton("Exportar Resultados");
        JButton btnImprimir = new JButton("Imprimir");
        
        estilizarBoton(btnExportar);
        estilizarBoton(btnImprimir);
        
        panelAcciones.add(btnExportar);
        panelAcciones.add(btnImprimir);
        
        panelResultados.add(panelAcciones, BorderLayout.SOUTH);
        
        panelBusqueda.add(panelFiltros, BorderLayout.NORTH);
        panelBusqueda.add(panelResultados, BorderLayout.CENTER);
        
        panelPestanas.addTab("Búsqueda Avanzada", new ImageIcon(), panelBusqueda, "Buscar muebles");
    }
    
    private void crearPanelEstadisticas() {
        panelEstadisticas = new JPanel(new BorderLayout(10, 10));
        panelEstadisticas.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelEstadisticas.setBackground(colorClaro);
        
        // Panel principal con GridLayout
        JPanel panelContenido = new JPanel(new GridLayout(2, 2, 15, 15));
        panelContenido.setBackground(colorClaro);
        
        // Panel 1: Ventas por categoría
        JPanel panelVentas = crearPanelEstadistica("Ventas por Categoría", "gráfico aquí");
        
        // Panel 2: Stock actual
        JPanel panelStock = crearPanelEstadistica("Nivel de Stock", "gráfico aquí");
        
        // Panel 3: Muebles más vendidos
        JPanel panelMasVendidos = crearPanelEstadistica("Muebles Más Vendidos", "tabla aquí");
        
        // Panel 4: Valor del inventario
        JPanel panelValor = crearPanelEstadistica("Valor del Inventario", "datos aquí");
        
        panelContenido.add(panelVentas);
        panelContenido.add(panelStock);
        panelContenido.add(panelMasVendidos);
        panelContenido.add(panelValor);
        
        // Panel superior con filtros de fecha
        JPanel panelFiltrosFecha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltrosFecha.setBackground(colorClaro);
        panelFiltrosFecha.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panelFiltrosFecha.add(new JLabel("Periodo:"));
        
        String[] periodos = {"Último mes", "Últimos 3 meses", "Último año", "Personalizado"};
        JComboBox<String> comboPeriodo = new JComboBox<>(periodos);
        panelFiltrosFecha.add(comboPeriodo);
        
        panelFiltrosFecha.add(new JLabel("Desde:"));
        JTextField campoDesde = new JTextField(10);
        panelFiltrosFecha.add(campoDesde);
        
        panelFiltrosFecha.add(new JLabel("Hasta:"));
        JTextField campoHasta = new JTextField(10);
        panelFiltrosFecha.add(campoHasta);
        
        JButton btnActualizar = new JButton("Actualizar");
        estilizarBoton(btnActualizar);
        panelFiltrosFecha.add(btnActualizar);
        
        // Panel inferior con acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAcciones.setBackground(colorClaro);
        
        JButton btnExportar = new JButton("Exportar Estadísticas");
        JButton btnImprimir = new JButton("Imprimir Reporte");
        
        estilizarBoton(btnExportar);
        estilizarBoton(btnImprimir);
        
        panelAcciones.add(btnExportar);
        panelAcciones.add(btnImprimir);
        
        panelEstadisticas.add(panelFiltrosFecha, BorderLayout.NORTH);
        panelEstadisticas.add(panelContenido, BorderLayout.CENTER);
        panelEstadisticas.add(panelAcciones, BorderLayout.SOUTH);
        
        panelPestanas.addTab("Estadísticas", new ImageIcon(), panelEstadisticas, "Ver estadísticas y reportes");
    }
    
    private JPanel crearPanelEstadistica(String titulo, String contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        
        JLabel lblContenido = new JLabel(contenido);
        lblContenido.setHorizontalAlignment(JLabel.CENTER);
        lblContenido.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        panel.add(lblContenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(colorPrimario);
        barra.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JLabel lblEstado = new JLabel("Sistema listo | 85 muebles en inventario | Último acceso: 21/04/2025 10:30");
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        barra.add(lblEstado, BorderLayout.WEST);
        
        JLabel lblUsuario = new JLabel("Usuario: Administrador | v1.0.0");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setHorizontalAlignment(JLabel.RIGHT);
        barra.add(lblUsuario, BorderLayout.EAST);
        
        return barra;
    }
    
    private void estilizarBoton(JButton boton) {
        estilizarBoton(boton, colorPrimario);
    }
    
    private void estilizarBoton(JButton boton, Color color) {
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBorder(new EmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });
    }
    
    private void configurarEventos() {
        // Evento para el botón guardar en el formulario
        for (Component comp : panelFormulario.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if (subComp instanceof JPanel && ((JPanel) subComp).getLayout() instanceof FlowLayout) {
                        for (Component btn : ((JPanel) subComp).getComponents()) {
                            if (btn instanceof JButton && ((JButton) btn).getText().equals("Guardar")) {
                                ((JButton) btn).addActionListener(e -> guardarNuevoMueble());
                            } else if (btn instanceof JButton && ((JButton) btn).getText().equals("Limpiar")) {
                                ((JButton) btn).addActionListener(e -> limpiarFormulario());
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void guardarNuevoMueble() {
        // Validación básica
        if (campoNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingrese el nombre del mueble", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (campoPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingrese el precio del mueble", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Validar que el precio sea un número
            double precio = Double.parseDouble(campoPrecio.getText().replace(",", "."));
            
            // Generar un ID aleatorio simulado
            int id = 1000 + (int)(Math.random() * 9000);
            
            // Añadir a la tabla de inventario
            Object[] fila = {
                String.valueOf(id),
                campoNombre.getText(),
                comboTipo.getSelectedItem(),
                comboMaterial.getSelectedItem(),
                comboColor.getSelectedItem(),
                String.valueOf(precio),
                spinnerCantidad.getValue(),
                checkDisponible.isSelected()
            };
            
            modeloTabla.addRow(fila);
            
            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, 
                "Mueble registrado correctamente con ID: " + id, 
                "Registro exitoso", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar formulario
            limpiarFormulario();
            
            // Cambiar a la pestaña de inventario
            panelPestanas.setSelectedIndex(0);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El precio debe ser un número válido. Use punto o coma para decimales.", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFormulario() {
        campoNombre.setText("");
        comboTipo.setSelectedIndex(0);
        comboMaterial.setSelectedIndex(0);
        comboColor.setSelectedIndex(0);
        campoPrecio.setText("");
        spinnerCantidad.setValue(1);
        checkDisponible.setSelected(true);
        campoDescripcion.setText("");
    }
    
    private void cargarDatosEjemplo() {
        Object[][] datos = {
            {"1001", "Silla Ergonómica Modelo Comfort", "Silla", "Metal", "Negro", "129.99", "15", true},
            {"1002", "Mesa de Centro Nordic", "Mesa", "Madera", "Blanco", "249.50", "8", true},
            {"1003", "Sofá 3 plazas London", "Sofá", "Tela", "Gris", "599.00", "3", true},
            {"1004", "Estantería Modular Cube", "Estantería", "Madera", "Marrón", "189.90", "12", true},
            {"1005", "Sillón Reclinable Relax", "Sofá", "Cuero", "Negro", "450.00", "5", true},
            {"1006", "Mesa Comedor Extensible Roma", "Mesa", "Madera", "Marrón", "349.00", "7", true},
            {"1007", "Armario Ropero 3 puertas Classic", "Armario", "Madera", "Blanco", "389.99", "4", true},
            {"1008", "Cama Doble Premium", "Cama", "Madera", "Beige", "499.99", "2", false},
            {"1009", "Escritorio Office Pro", "Escritorio", "Metal", "Negro", "229.90", "10", true},
            {"1010", "Mesita de Noche Minimal", "Mesa", "Madera", "Blanco", "89.99", "20", true}
        };
        
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }
    
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            Main sistema = new Main();
            sistema.setVisible(true);
        });
    }
}

// Clase para manejar el modelo de datos de muebles
class Mueble {
    private int id;
    private String nombre;
    private String tipo;
    private String material;
    private String color;
    private double precio;
    private int stock;
    private boolean disponible;
    private String descripcion;
    private List<String> imagenes;
    
    public Mueble(int id, String nombre, String tipo, String material, String color, 
                 double precio, int stock, boolean disponible, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.material = material;
        this.color = color;
        this.precio = precio;
        this.stock = stock;
        this.disponible = disponible;
        this.descripcion = descripcion;
        this.imagenes = new ArrayList<>();
    }
    
    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
    public void addImagen(String ruta) { this.imagenes.add(ruta); }
    
    @Override
    public String toString() {
        return "Mueble [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + 
               ", precio=" + precio + ", stock=" + stock + "]";
    }
}

// Clase para gestionar la persistencia de datos (simulada)
class GestorDatos {
    private List<Mueble> inventario;
    
    public GestorDatos() {
        this.inventario = new ArrayList<>();
    }
    
    public void agregarMueble(Mueble mueble) {
        inventario.add(mueble);
    }
    
    public void eliminarMueble(int id) {
        inventario.removeIf(m -> m.getId() == id);
    }
    
    public Mueble buscarMueblePorId(int id) {
        return inventario.stream()
                        .filter(m -> m.getId() == id)
                        .findFirst()
                        .orElse(null);
    }
    
    public List<Mueble> buscarMuebles(String criterio, String valor) {
        List<Mueble> resultados = new ArrayList<>();
        
        switch (criterio.toLowerCase()) {
            case "nombre":
                resultados = inventario.stream()
                            .filter(m -> m.getNombre().toLowerCase().contains(valor.toLowerCase()))
                            .collect(Collectors.toList());
                break;
            case "tipo":
                resultados = inventario.stream()
                            .filter(m -> m.getTipo().equalsIgnoreCase(valor))
                            .collect(Collectors.toList());
                break;
            case "material":
                resultados = inventario.stream()
                            .filter(m -> m.getMaterial().equalsIgnoreCase(valor))
                            .collect(Collectors.toList());
                break;
            case "color":
                resultados = inventario.stream()
                            .filter(m -> m.getColor().equalsIgnoreCase(valor))
                            .collect(Collectors.toList());
                break;
            case "disponible":
                boolean disponible = Boolean.parseBoolean(valor);
                resultados = inventario.stream()
                            .filter(m -> m.isDisponible() == disponible)
                            .collect(Collectors.toList());
                break;
            default:
                resultados = inventario;
        }
        
        return resultados;
    }
    
    public List<Mueble> getInventarioCompleto() {
        return new ArrayList<>(inventario);
    }
    
    public void guardarDatos() {
        // Simulación de guardado en archivo o base de datos
        System.out.println("Guardando datos... " + inventario.size() + " registros guardados.");
    }
    
    public void cargarDatos() {
        // Simulación de carga desde archivo o base de datos
        System.out.println("Cargando datos...");
        // Aquí se cargarían los datos reales
    }
}

// Clase para generar reportes (simulada)
class GeneradorReportes {
    public void generarReporteInventario(List<Mueble> inventario, String rutaArchivo) {
        // Simulación de generación de reporte
        System.out.println("Generando reporte de inventario en " + rutaArchivo);
        System.out.println("Total de muebles: " + inventario.size());
        
        double valorTotal = inventario.stream()
                            .mapToDouble(m -> m.getPrecio() * m.getStock())
                            .sum();
        
        System.out.println("Valor total del inventario: " + valorTotal + " €");
    }
    
    public void generarReporteVentas(String periodo, String rutaArchivo) {
        // Simulación de generación de reporte de ventas
        System.out.println("Generando reporte de ventas para el periodo: " + periodo);
        System.out.println("Reporte guardado en: " + rutaArchivo);
    }
    
    public void exportarDatosExcel(List<Mueble> datos, String rutaArchivo) {
        // Simulación de exportación a Excel
        System.out.println("Exportando " + datos.size() + " registros a Excel");
        System.out.println("Archivo guardado en: " + rutaArchivo);
    }
}

// Clase para detalles de mueble (para vista detallada)
class DetalleMuebleDialog extends JDialog {
    private Mueble mueble;
    private Color colorPrimario = new Color(61, 90, 128);
    private Color colorSecundario = new Color(152, 193, 217);
    private Color colorClaro = new Color(224, 251, 252);
    
    public DetalleMuebleDialog(Frame parent, Mueble mueble) {
        super(parent, "Detalle de Mueble", true);
        this.mueble = mueble;
        
        inicializarComponentes();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }
    
    private void inicializarComponentes() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(colorClaro);
        
        // Información básica
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            "Información del Mueble",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Mostrar información
        agregarCampo(panelInfo, gbc, 0, "ID:", String.valueOf(mueble.getId()));
        agregarCampo(panelInfo, gbc, 1, "Nombre:", mueble.getNombre());
        agregarCampo(panelInfo, gbc, 2, "Tipo:", mueble.getTipo());
        agregarCampo(panelInfo, gbc, 3, "Material:", mueble.getMaterial());
        agregarCampo(panelInfo, gbc, 4, "Color:", mueble.getColor());
        agregarCampo(panelInfo, gbc, 5, "Precio:", mueble.getPrecio() + " €");
        agregarCampo(panelInfo, gbc, 6, "Stock:", String.valueOf(mueble.getStock()));
        agregarCampo(panelInfo, gbc, 7, "Disponible:", mueble.isDisponible() ? "Sí" : "No");
        
        // Panel para descripción
        JPanel panelDesc = new JPanel(new BorderLayout());
        panelDesc.setBackground(Color.WHITE);
        panelDesc.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            "Descripción",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        
        JTextArea areaDesc = new JTextArea(mueble.getDescripcion());
        areaDesc.setEditable(false);
        areaDesc.setLineWrap(true);
        areaDesc.setWrapStyleWord(true);
        areaDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaDesc.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        panelDesc.add(new JScrollPane(areaDesc), BorderLayout.CENTER);
        
        // Panel para imágenes
        JPanel panelImagenes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelImagenes.setBackground(Color.WHITE);
        panelImagenes.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorSecundario),
            "Imágenes",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            colorPrimario
        ));
        
        // Simular imágenes
        for (int i = 0; i < 3; i++) {
            JPanel imgPanel = new JPanel();
            imgPanel.setPreferredSize(new Dimension(200, 200));
            imgPanel.setBackground(new Color(240, 240, 240));
            imgPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
            
            JLabel lblImg = new JLabel("Imagen " + (i+1));
            lblImg.setHorizontalAlignment(JLabel.CENTER);
            imgPanel.add(lblImg);
            
            panelImagenes.add(imgPanel);
        }
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(colorClaro);
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(colorSecundario);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(colorPrimario);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnEditar);
        panelBotones.add(btnCerrar);
        
        // Añadir componentes al panel principal
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setBackground(colorClaro);
        panelCentro.add(panelInfo, BorderLayout.NORTH);
        panelCentro.add(panelDesc, BorderLayout.CENTER);
        
        panel.add(panelImagenes, BorderLayout.NORTH);
        panel.add(panelCentro, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, String valor) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblValor, gbc);
    }
}

// Clase de utilidad para manejar imágenes
class ImagenUtil {
    public static ImageIcon redimensionarImagen(ImageIcon icono, int ancho, int alto) {
        Image img = icono.getImage();
        Image imgRedimensionada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imgRedimensionada);
    }
    
    public static void guardarImagen(Image imagen, String ruta) {
        // Simulación de guardado de imagen
        System.out.println("Guardando imagen en: " + ruta);
    }
}
