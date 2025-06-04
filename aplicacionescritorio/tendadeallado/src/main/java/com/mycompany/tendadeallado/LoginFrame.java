package com.mycompany.tendadeallado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginFrame() {
        setTitle("Sistema de Gestión - Login");
        setSize(360, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal con fondo blanco
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título con fuente sobria
        JLabel titleLabel = new JLabel("Iniciar Sesión");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel formulario centrado
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        userField = new JTextField(15);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        loginButton = new JButton("Entrar");
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(45, 118, 232));  // Azul Windows clásico
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setForeground(Color.GRAY);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listeners
        loginButton.addActionListener(e -> verificarLogin());
        cancelButton.addActionListener(e -> {
            userField.setText("");
            passwordField.setText("");
        });

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
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
