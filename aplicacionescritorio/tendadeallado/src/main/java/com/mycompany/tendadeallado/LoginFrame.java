package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginFrame() {
        setTitle("Sistema de Gestión de Tienda - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel logoPanel = new JPanel();
        JLabel logoLabel = new JLabel("SISTEMA DE GESTIÓN");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoPanel.add(logoLabel);
        
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
        
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        loginButton.addActionListener(e -> verificarLogin());
        cancelButton.addActionListener(e -> {
            userField.setText("");
            passwordField.setText("");
        });

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    verificarLogin();
                }
            }
        });
    }

    private void verificarLogin() {
        String usuario = userField.getText().trim();
        String inputPassword = new String(passwordField.getPassword());

        if (usuario.isEmpty() || inputPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        String query = "SELECT Nombre, Contrasena FROM Empleados WHERE Usuario = ?";

        try (Connection conn = MainFrame.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("Nombre");
                String hashedPassword = rs.getString("Contrasena");

                // Aquí comprobamos si la contraseña está cifrada con bcrypt (empieza por $2a$, $2b$, etc)
                if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
                    if (BCrypt.checkpw(inputPassword, hashedPassword)) {
                        accesoExitoso(nombre);
                    } else {
                        mostrarErrorContrasena();
                    }
                } else {
                    // Si no está cifrada (texto plano), comparamos directamente
                    if (inputPassword.equals(hashedPassword)) {
                        accesoExitoso(nombre);
                    } else {
                        mostrarErrorContrasena();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No existe un usuario con ese nombre.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.");
        }
    }

    private void accesoExitoso(String nombre) {
        JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso. ¡Bienvenido " + nombre + "!");
        MainFrame mainFrame = new MainFrame(nombre);
        mainFrame.setVisible(true);
        this.dispose();
    }

    private void mostrarErrorContrasena() {
        JOptionPane.showMessageDialog(this, "Contraseña incorrecta.");
        passwordField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
