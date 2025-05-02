/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PRACTICAS
 */
// Archivo: LoginFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private HashMap<String, String> users;

    public LoginFrame() {
        // Configuración de usuarios para la simulación (en un sistema real esto estaría en una BD)
        users = new HashMap<>();
        users.put("admin", "admin123");
        users.put("vendedor", "venta123");
        
        // Configuración de la ventana
        setTitle("Sistema de Gestión de Tienda - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de logo (simulado)
        JPanel logoPanel = new JPanel();
        JLabel logoLabel = new JLabel("SISTEMA DE GESTIÓN");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoPanel.add(logoLabel);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel userLabel = new JLabel("Usuario:");
        userField = new JTextField(15);
        
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordField = new JPasswordField(15);
        
        loginButton = new JButton("Iniciar Sesión");
        cancelButton = new JButton("Cancelar");
        
        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(cancelButton);
        
        // Añadir componentes al panel principal
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Agregar panel principal a la ventana
        add(mainPanel);
        
        // Configurar acciones de botones
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verificarLogin();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userField.setText("");
                passwordField.setText("");
            }
        });
        
        // Hacer que al presionar Enter se active el botón de login
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    verificarLogin();
                }
            }
        });
    }
    
    private void verificarLogin() {
        String username = userField.getText();
        String password = new String(passwordField.getPassword());
        
        if (users.containsKey(username) && users.get(username).equals(password)) {
            JOptionPane.showMessageDialog(this, 
                "Inicio de sesión exitoso", 
                "Acceso Concedido", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir ventana principal
            MainFrame mainFrame = new MainFrame(username);
            mainFrame.setVisible(true);
            this.dispose(); // Cerrar ventana de login
        } else {
            JOptionPane.showMessageDialog(this, 
                "Usuario o contraseña incorrectos", 
                "Error de Acceso", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
