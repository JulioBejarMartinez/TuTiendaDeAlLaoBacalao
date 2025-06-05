package com.mycompany.tendadeallado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class InventoryPanel extends JPanel {
    private JLabel statusBar;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Proveedor> proveedores;
    private ConfigReader configReader;


    public InventoryPanel(JLabel statusBar, ConfigReader configReader) {
        this.statusBar = statusBar;
        this.proveedores = new ArrayList<>();
        this.configReader = configReader;

        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Gestión de Inventario");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Imagen", "Nombre", "Descripción", "Precio", "Stock", "Stock Mín.", "Categoría", "Proveedor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable directamente
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) { // Columna de imagen
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(80); // Aumentar altura de filas para las imágenes
        
        // Configurar renderer para la columna de imagen
        table.getColumnModel().getColumn(1).setCellRenderer(new ImageTableCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setMinWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnNuevo = new JButton("Nuevo Producto");
        JButton btnEditar = new JButton("Editar Seleccionado");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnImportar = new JButton("Importar CSV");
        JButton btnExportar = new JButton("Exportar CSV");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnVerImagen = new JButton("Ver Imagen Grande");

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnImportar);
        buttonPanel.add(btnExportar);
        buttonPanel.add(btnRefrescar);
        buttonPanel.add(btnVerImagen);

        // Añadir componentes
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidades de botones
        btnNuevo.addActionListener(e -> crearNuevoProducto());
        btnEditar.addActionListener(e -> editarProductoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarProductoSeleccionado());
        btnImportar.addActionListener(e -> importarProductos());
        btnExportar.addActionListener(e -> exportarProductos());
        btnRefrescar.addActionListener(e -> cargarProductosDesdeBaseDeDatos());
        btnVerImagen.addActionListener(e -> mostrarImagenGrande());

        // Cargar proveedores y productos desde la base de datos
        cargarProveedores();
        cargarProductosDesdeBaseDeDatos();
    }

    private void cargarProveedores() {
        proveedores.clear();
        try {
    String url = "jdbc:mysql://" + configReader.getDbHost() + ":" +
                 configReader.getDbPort() + "/" +
                 configReader.getDbName() + "?useSSL=false&serverTimezone=UTC";

    Connection conn = DriverManager.getConnection(url, configReader.getDbUser(), configReader.getDbPassword());

    try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID_Proveedor, Nombre FROM Proveedores")) {

               while (rs.next()) {
                   proveedores.add(new Proveedor(rs.getInt("ID_Proveedor"), rs.getString("Nombre")));
               }

           } finally {
               conn.close();
           }

       } catch (SQLException e) {
           System.err.println("Error al cargar proveedores: " + e.getMessage());
       }
    }


    private void cargarProductosDesdeBaseDeDatos() {
    tableModel.setRowCount(0);

    try {
        String url = "jdbc:mysql://" + configReader.getDbHost() + ":" +
                     configReader.getDbPort() + "/" +
                     configReader.getDbName() + "?useSSL=false&serverTimezone=UTC";
        Connection conn = DriverManager.getConnection(url, configReader.getDbUser(), configReader.getDbPassword());

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT p.ID_Producto, p.Nombre, p.Descripcion, p.PrecioProducto, " +
                 "p.StockActual, p.StockMinimo, p.Tipo, p.ImagenProducto, pr.Nombre as NombreProveedor " +
                 "FROM Productos p LEFT JOIN Proveedores pr ON p.ID_Proveedor = pr.ID_Proveedor")) {

            while (rs.next()) {
                ImageIcon imagen = cargarImagenProducto(rs.getString("ImagenProducto"));

                Object[] row = {
                    rs.getInt("ID_Producto"),
                    imagen,
                    rs.getString("Nombre"),
                    rs.getString("Descripcion"),
                    String.format("%.2f €", rs.getDouble("PrecioProducto")),
                    rs.getInt("StockActual"),
                    rs.getInt("StockMinimo"),
                    rs.getString("Tipo"),
                    rs.getString("NombreProveedor")
                };
                tableModel.addRow(row);
            }

            if (statusBar != null) {
                statusBar.setText(" Gestión de Inventario - " + tableModel.getRowCount() + " productos en la lista");
            }

        } finally {
            conn.close();
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


    private ImageIcon cargarImagenProducto(String rutaImagen) {
    if (rutaImagen == null || rutaImagen.trim().isEmpty()) {
        return crearImagenPorDefecto();
    }

    try {
        ImageIcon icon = null;

        // Si no es una URL completa, construir la URL desde GitHub
        if (!rutaImagen.startsWith("http://") && !rutaImagen.startsWith("https://") && !rutaImagen.startsWith("ftp://")) {
            rutaImagen = "https://raw.githubusercontent.com/JulioBejarMartinez/TuTiendaDeAlLaoBacalao/master/" + rutaImagen;
        }

        URL url = new URL(rutaImagen);
        BufferedImage img = ImageIO.read(url);
        if (img != null) {
            icon = new ImageIcon(redimensionarImagen(img, 70, 70));
        }

        return icon != null ? icon : crearImagenPorDefecto();

    } catch (Exception e) {
        System.err.println("Error al cargar imagen: " + rutaImagen + " - " + e.getMessage());
        return crearImagenPorDefecto();
    }
}


    private ImageIcon crearImagenPorDefecto() {
        BufferedImage img = new BufferedImage(70, 70, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 70, 70);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(0, 0, 69, 69);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("Sin", 25, 30);
        g2d.drawString("Imagen", 17, 45);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private BufferedImage redimensionarImagen(BufferedImage original, int ancho, int alto) {
        BufferedImage redimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = redimensionada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, ancho, alto, null);
        g2d.dispose();
        return redimensionada;
    }

    private void mostrarImagenGrande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para ver su imagen.", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nombreProducto = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Obtener la ruta de la imagen de la base de datos
        String rutaImagen = obtenerRutaImagenProducto(id);
        
        if (rutaImagen != null && !rutaImagen.trim().isEmpty()) {
            mostrarDialogoImagen(rutaImagen, nombreProducto);
        } else {
            JOptionPane.showMessageDialog(this, "Este producto no tiene imagen asociada.", 
                "Sin imagen", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String obtenerRutaImagenProducto(int idProducto) {
        try {
    String url = "jdbc:mysql://" + configReader.getDbHost() + ":" +
                 configReader.getDbPort() + "/" +
                 configReader.getDbName() + "?useSSL=false&serverTimezone=UTC";

    Connection conn = DriverManager.getConnection(url, configReader.getDbUser(), configReader.getDbPassword());

    try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT ImagenProducto FROM Productos WHERE ID_Producto = ?")) {

        stmt.setInt(1, idProducto);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getString("ImagenProducto");
        }

            } finally {
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void mostrarDialogoImagen(String rutaImagen, String nombreProducto) {
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Imagen - " + nombreProducto);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    try {
        ImageIcon icon = null;

        // Si no es URL completa, convertir a raw.githubusercontent
        if (!rutaImagen.startsWith("http://") && !rutaImagen.startsWith("https://") && !rutaImagen.startsWith("ftp://")) {
            rutaImagen = "https://raw.githubusercontent.com/JulioBejarMartinez/TuTiendaDeAlLaoBacalao/master/" + rutaImagen;
        }

        URL url = new URL(rutaImagen);
        BufferedImage img = ImageIO.read(url);
        if (img != null) {
            // Redimensionar manteniendo proporción para vista grande
            int maxWidth = 400;
            int maxHeight = 400;
            double scale = Math.min((double) maxWidth / img.getWidth(), (double) maxHeight / img.getHeight());
            int newWidth = (int) (img.getWidth() * scale);
            int newHeight = (int) (img.getHeight() * scale);
            icon = new ImageIcon(redimensionarImagen(img, newWidth, newHeight));
        }

        if (icon != null) {
            JLabel labelImagen = new JLabel(icon);
            labelImagen.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            dialog.add(labelImagen);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al mostrar la imagen: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void crearNuevoProducto() {
        ProductoDialog dialog = new ProductoDialog(SwingUtilities.getWindowAncestor(this), proveedores);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Producto producto = dialog.getProducto();
            if (insertarProducto(producto)) {
                cargarProductosDesdeBaseDeDatos();
                if (statusBar != null) {
                    statusBar.setText(" Nuevo producto agregado correctamente");
                }
            }
        }
    }

    private void editarProductoSeleccionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Producto producto = obtenerProductoPorId(id);
        
        if (producto != null) {
            ProductoDialog dialog = new ProductoDialog(SwingUtilities.getWindowAncestor(this), proveedores, producto);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Producto productoEditado = dialog.getProducto();
                productoEditado.setId(id);
                if (actualizarProducto(productoEditado)) {
                    cargarProductosDesdeBaseDeDatos();
                    if (statusBar != null) {
                        statusBar.setText(" Producto actualizado correctamente");
                    }
                }
            }
        }
    }

    private void eliminarProductoSeleccionado() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", 
            "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, 
        "¿Estás seguro de que quieres eliminar este producto?", 
        "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
    if (confirm == JOptionPane.YES_OPTION) {
        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            String url = "jdbc:mysql://" + configReader.getDbHost() + ":" +
                         configReader.getDbPort() + "/" +
                         configReader.getDbName() + "?useSSL=false&serverTimezone=UTC";

            try (Connection conn = DriverManager.getConnection(url, configReader.getDbUser(), configReader.getDbPassword());
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Productos WHERE ID_Producto = ?")) {

                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    if (statusBar != null) {
                        statusBar.setText(" Producto eliminado");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


    private void importarProductos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int imported = 0;
                int errors = 0;
                
                // Saltar la primera línea si contiene encabezados
                br.readLine();
                
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    
                    if (data.length >= 8) { // Ahora incluye ImagenProducto
                        try {
                            Producto producto = new Producto();
                            producto.setNombre(data[0].trim());
                            producto.setDescripcion(data[1].trim());
                            producto.setPrecio(Double.parseDouble(data[2].trim()));
                            producto.setStockActual(Integer.parseInt(data[3].trim()));
                            producto.setStockMinimo(Integer.parseInt(data[4].trim()));
                            producto.setTipo(data[5].trim());
                            producto.setIdProveedor(Integer.parseInt(data[6].trim()));
                            producto.setImagenProducto(data[7].trim());
                            
                            if (insertarProducto(producto)) {
                                imported++;
                            } else {
                                errors++;
                            }
                        } catch (NumberFormatException e) {
                            errors++;
                        }
                    } else {
                        errors++;
                    }
                }
                
                cargarProductosDesdeBaseDeDatos();
                JOptionPane.showMessageDialog(this, 
                    String.format("Importación completada:\n%d productos importados\n%d errores", 
                    imported, errors), "Resultado", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarProductos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        fileChooser.setSelectedFile(new File("productos.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                // Escribir encabezados
                pw.println("Nombre,Descripcion,Precio,StockActual,StockMinimo,Tipo,ID_Proveedor,ImagenProducto");
                
                // Escribir datos
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String nombre = (String) tableModel.getValueAt(i, 2);
                    String descripcion = (String) tableModel.getValueAt(i, 3);
                    String precio = ((String) tableModel.getValueAt(i, 4)).replace(" €", "").replace(",", ".");
                    int stockActual = (Integer) tableModel.getValueAt(i, 5);
                    int stockMinimo = (Integer) tableModel.getValueAt(i, 6);
                    String tipo = (String) tableModel.getValueAt(i, 7);
                    
                    // Obtener ID del proveedor e imagen
                    int idProducto = (Integer) tableModel.getValueAt(i, 0);
                    int idProveedor = obtenerIdProveedorPorProducto(idProducto);
                    String imagenProducto = obtenerRutaImagenProducto(idProducto);
                    
                    pw.println(String.format("%s,%s,%s,%d,%d,%s,%d,%s", 
                        nombre, descripcion, precio, stockActual, stockMinimo, tipo, idProveedor, 
                        imagenProducto != null ? imagenProducto : ""));
                }
                
                JOptionPane.showMessageDialog(this, "Productos exportados correctamente a: " + file.getAbsolutePath(), 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al exportar productos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean insertarProducto(Producto producto) {
    try {
        String url = "jdbc:mysql://" + configReader.getDbHost() + ":" +
                     configReader.getDbPort() + "/" +
                     configReader.getDbName() + "?useSSL=false&serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(url, configReader.getDbUser(), configReader.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO Productos (Nombre, Descripcion, PrecioProducto, StockActual, StockMinimo, Tipo, ID_Proveedor, ImagenProducto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStockActual());
            stmt.setInt(5, producto.getStockMinimo());
            stmt.setString(6, producto.getTipo());
            stmt.setInt(7, producto.getIdProveedor());
            stmt.setString(8, producto.getImagenProducto());

            return stmt.executeUpdate() > 0;
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al insertar producto: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}


    private boolean actualizarProducto(Producto producto) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE Productos SET Nombre=?, Descripcion=?, PrecioProducto=?, StockActual=?, StockMinimo=?, Tipo=?, ID_Proveedor=?, ImagenProducto=? WHERE ID_Producto=?")) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStockActual());
            stmt.setInt(5, producto.getStockMinimo());
            stmt.setString(6, producto.getTipo());
            stmt.setInt(7, producto.getIdProveedor());
            stmt.setString(8, producto.getImagenProducto());
            stmt.setInt(9, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Producto obtenerProductoPorId(int id) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM Productos WHERE ID_Producto = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("ID_Producto"));
                producto.setNombre(rs.getString("Nombre"));
                producto.setDescripcion(rs.getString("Descripcion"));
                producto.setPrecio(rs.getDouble("PrecioProducto"));
                producto.setStockActual(rs.getInt("StockActual"));
                producto.setStockMinimo(rs.getInt("StockMinimo"));
                producto.setTipo(rs.getString("Tipo"));
                producto.setIdProveedor(rs.getInt("ID_Proveedor"));
                producto.setImagenProducto(rs.getString("ImagenProducto"));
                return producto;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int obtenerIdProveedorPorProducto(int idProducto) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT ID_Proveedor FROM Productos WHERE ID_Producto = ?")) {

            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID_Proveedor");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Renderer personalizado para mostrar imágenes en la tabla
    private static class ImageTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                
                if (isSelected) {
                    label.setOpaque(true);
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setOpaque(false);
                }
                
                return label;
            }
            
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    // Clases auxiliares
    public static class Producto {
        private int id;
        private String nombre;
        private String descripcion;
        private double precio;
        private int stockActual;
        private int stockMinimo;
        private String tipo;
        private int idProveedor;
        private String imagenProducto;

        // Getters y setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }
        public int getStockActual() { return stockActual; }
        public void setStockActual(int stockActual) { this.stockActual = stockActual; }
        public int getStockMinimo() { return stockMinimo; }
        public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public int getIdProveedor() { return idProveedor; }
        public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
        public String getImagenProducto() { return imagenProducto; }
        public void setImagenProducto(String imagenProducto) { this.imagenProducto = imagenProducto; }
    }

    public static class Proveedor {
        private int id;
        private String nombre;

        public Proveedor(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }

        @Override
        public String toString() {
            return nombre;
        }
    }

    // Diálogo para crear/editar productos
    private static class ProductoDialog extends JDialog {
        private JTextField txtNombre;
        private JTextArea txtDescripcion;
        private JTextField txtPrecio;
        private JTextField txtStockActual;
        private JTextField txtStockMinimo;
        private JComboBox<String> cmbTipo;
        private JComboBox<Proveedor> cmbProveedor;
        private JTextField txtImagenProducto;
        private JButton btnSeleccionarImagen;
        private JLabel lblVistaPrevia;
        private boolean confirmed = false;

        public ProductoDialog(Window parent, List<Proveedor> proveedores) {
            this(parent, proveedores, null);
        }

        public ProductoDialog(Window parent, List<Proveedor> proveedores, Producto producto) {
            super(parent, producto == null ? "Nuevo Producto" : "Editar Producto", ModalityType.APPLICATION_MODAL);
            
            initComponents(proveedores);
            
            if (producto != null) {
                cargarDatosProducto(producto);
            }
            
            pack();
            setLocationRelativeTo(parent);
        }

        private void initComponents(List<Proveedor> proveedores) {
            setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Nombre
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Nombre:"), gbc);
            gbc.gridx = 1;
            txtNombre = new JTextField(20);
            mainPanel.add(txtNombre, gbc);

            // Descripción
            gbc.gridx = 0; gbc.gridy = 1;
            mainPanel.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1;
            txtDescripcion = new JTextArea(3, 20);
            txtDescripcion.setLineWrap(true);
            txtDescripcion.setWrapStyleWord(true);
            mainPanel.add(new JScrollPane(txtDescripcion), gbc);

            // Precio
            gbc.gridx = 0; gbc.gridy = 2;
            mainPanel.add(new JLabel("Precio:"), gbc);
            gbc.gridx = 1;
            txtPrecio = new JTextField(20);
            mainPanel.add(txtPrecio, gbc);

            // Stock Actual
            gbc.gridx = 0; gbc.gridy = 3;
            mainPanel.add(new JLabel("Stock Actual:"), gbc);
            gbc.gridx = 1;
            txtStockActual = new JTextField(20);
            mainPanel.add(txtStockActual, gbc);

            // Stock Mínimo
            gbc.gridx = 0; gbc.gridy = 4;
            mainPanel.add(new JLabel("Stock Mínimo:"), gbc);
            gbc.gridx = 1;
            txtStockMinimo = new JTextField(20);
            mainPanel.add(txtStockMinimo, gbc);

            // Tipo
            gbc.gridx = 0; gbc.gridy = 5;
            mainPanel.add(new JLabel("Categoría:"), gbc);
            gbc.gridx = 1;
            String[] tipos = {"Conservas", "Bebidas", "Higiene", "Snacks", "Dulces", "Refrigerado"};
            cmbTipo = new JComboBox<>(tipos);
            mainPanel.add(cmbTipo, gbc);

            // Proveedor
            gbc.gridx = 0; gbc.gridy = 6;
            mainPanel.add(new JLabel("Proveedor:"), gbc);
            gbc.gridx = 1;
            cmbProveedor = new JComboBox<>(proveedores.toArray(new Proveedor[0]));
            mainPanel.add(cmbProveedor, gbc);

            // Imagen del producto
            gbc.gridx = 0; gbc.gridy = 7;
            mainPanel.add(new JLabel("Imagen:"), gbc);
            gbc.gridx = 1;
            JPanel panelImagen = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            txtImagenProducto = new JTextField(15);
            btnSeleccionarImagen = new JButton("...");
            btnSeleccionarImagen.setPreferredSize(new Dimension(30, txtImagenProducto.getPreferredSize().height));
            panelImagen.add(txtImagenProducto);
            panelImagen.add(btnSeleccionarImagen);
            mainPanel.add(panelImagen, gbc);

            // Vista previa de la imagen
            gbc.gridx = 0; gbc.gridy = 8;
            mainPanel.add(new JLabel("Vista previa:"), gbc);
            gbc.gridx = 1;
            lblVistaPrevia = new JLabel();
            lblVistaPrevia.setPreferredSize(new Dimension(100, 100));
            lblVistaPrevia.setBorder(BorderFactory.createLoweredBevelBorder());
            lblVistaPrevia.setHorizontalAlignment(JLabel.CENTER);
            lblVistaPrevia.setText("Sin imagen");
            mainPanel.add(lblVistaPrevia, gbc);

            // Eventos para la imagen
            btnSeleccionarImagen.addActionListener(e -> seleccionarImagen());
            txtImagenProducto.addActionListener(e -> actualizarVistaPrevia());
            txtImagenProducto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            });

            // Botones
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton btnGuardar = new JButton("Guardar");
            JButton btnCancelar = new JButton("Cancelar");

            btnGuardar.addActionListener(e -> {
                if (validarCampos()) {
                    confirmed = true;
                    dispose();
                }
            });

            btnCancelar.addActionListener(e -> dispose());

            buttonPanel.add(btnGuardar);
            buttonPanel.add(btnCancelar);

            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void seleccionarImagen() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif", "bmp"));
            
            int opcion = JOptionPane.showOptionDialog(this, 
                "¿Cómo desea especificar la imagen?", 
                "Seleccionar imagen", 
                JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                new String[]{"Archivo local", "URL/FTP", "Cancelar"}, 
                "Archivo local");
                
            if (opcion == 0) { // Archivo local
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    txtImagenProducto.setText(file.getAbsolutePath());
                    actualizarVistaPrevia();
                }
            } else if (opcion == 1) { // URL/FTP
                String url = JOptionPane.showInputDialog(this, 
                    "Ingrese la URL de la imagen:", 
                    "URL de imagen", 
                    JOptionPane.PLAIN_MESSAGE);
                if (url != null && !url.trim().isEmpty()) {
                    txtImagenProducto.setText(url.trim());
                    actualizarVistaPrevia();
                }
            }
        }

        private void actualizarVistaPrevia() {
    String rutaImagen = txtImagenProducto.getText().trim();

    if (rutaImagen.isEmpty()) {
        lblVistaPrevia.setIcon(null);
        lblVistaPrevia.setText("Sin imagen");
        return;
    }

    SwingUtilities.invokeLater(() -> {
        try {
            ImageIcon icon = null;

            // Si es ruta relativa tipo: "imagenes/xxx.jpg"
            String rutaFinal;
            if (!rutaImagen.startsWith("http://") && !rutaImagen.startsWith("https://") && !rutaImagen.startsWith("ftp://")) {
                // Convertir a URL RAW de GitHub
                rutaFinal = "https://raw.githubusercontent.com/JulioBejarMartinez/TuTiendaDeAlLaoBacalao/master/" + rutaImagen;
            } else {
                rutaFinal = rutaImagen;
            }

            URL url = new URL(rutaFinal);
            BufferedImage img = ImageIO.read(url);
            if (img != null) {
                icon = new ImageIcon(redimensionarImagen(img, 90, 90));
            }

            if (icon != null) {
                lblVistaPrevia.setIcon(icon);
                lblVistaPrevia.setText("");
            } else {
                lblVistaPrevia.setIcon(null);
                lblVistaPrevia.setText("Error al cargar");
            }

        } catch (Exception e) {
            lblVistaPrevia.setIcon(null);
            lblVistaPrevia.setText("Error al cargar");
        }
    });
}


        private BufferedImage redimensionarImagen(BufferedImage original, int ancho, int alto) {
            BufferedImage redimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = redimensionada.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(original, 0, 0, ancho, alto, null);
            g2d.dispose();
            return redimensionada;
        }

        private void cargarDatosProducto(Producto producto) {
            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
            txtStockActual.setText(String.valueOf(producto.getStockActual()));
            txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
            cmbTipo.setSelectedItem(producto.getTipo());
            txtImagenProducto.setText(producto.getImagenProducto() != null ? producto.getImagenProducto() : "");
            
            // Seleccionar proveedor
            for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
                if (cmbProveedor.getItemAt(i).getId() == producto.getIdProveedor()) {
                    cmbProveedor.setSelectedIndex(i);
                    break;
                }
            }
            
            // Actualizar vista previa
            actualizarVistaPrevia();
        }

        private boolean validarCampos() {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                return false;
            }

            try {
                Double.parseDouble(txtPrecio.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido");
                return false;
            }

            try {
                Integer.parseInt(txtStockActual.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El stock actual debe ser un número entero");
                return false;
            }

            try {
                Integer.parseInt(txtStockMinimo.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El stock mínimo debe ser un número entero");
                return false;
            }

            if (cmbProveedor.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor");
                return false;
            }

            return true;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public Producto getProducto() {
            Producto producto = new Producto();
            producto.setNombre(txtNombre.getText().trim());
            producto.setDescripcion(txtDescripcion.getText().trim());
            producto.setPrecio(Double.parseDouble(txtPrecio.getText()));
            producto.setStockActual(Integer.parseInt(txtStockActual.getText()));
            producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText()));
            producto.setTipo((String) cmbTipo.getSelectedItem());
            producto.setIdProveedor(((Proveedor) cmbProveedor.getSelectedItem()).getId());
            producto.setImagenProducto(txtImagenProducto.getText().trim());
            return producto;
        }
    }
}