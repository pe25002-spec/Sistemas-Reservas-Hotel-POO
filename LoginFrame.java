/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.vista;

import com.hotel.controlador.GestorHotel;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private GestorHotel gestor;

    public LoginFrame() {
        gestor = new GestorHotel();
        setTitle("Login - Sistema de Reservas de Hotel");
        setSize(380, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        panel.add(new JLabel("Usuario:"));
        txtUser = new JTextField();
        panel.add(txtUser);

        panel.add(new JLabel("Contraseña:"));
        txtPass = new JPasswordField();
        panel.add(txtPass);

        JButton btnLogin = new JButton("Iniciar Sesion");
        panel.add(new JLabel(""));
        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete ambos campos.", "Validacion", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (gestor.validarLogin(u, p)) {
                dispose();
                new SistemaHotelFrame(gestor).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

