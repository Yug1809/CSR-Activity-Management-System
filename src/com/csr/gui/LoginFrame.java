package com.csr.gui;

import com.csr.service.LoginService;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton clearButton;

    public LoginFrame() {

        // Window settings
        setTitle("CSR Activity Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // ---------------- HEADER ----------------
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(45, 62, 80));

        JLabel titleLabel = new JLabel(
                "CSR ACTIVITY MANAGEMENT SYSTEM",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(
                "Login to continue",
                SwingConstants.CENTER
        );

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // ---------------- FORM ----------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formPanel.add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;

        formPanel.add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;

        formPanel.add(passwordField, gbc);

        // ---------------- BUTTONS ----------------
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 247, 250));

        loginButton = new JButton("LOGIN");
        clearButton = new JButton("CLEAR");

        loginButton.setPreferredSize(new Dimension(110, 35));
        clearButton.setPreferredSize(new Dimension(110, 35));

        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        clearButton.setFont(new Font("Arial", Font.BOLD, 13));

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        formPanel.add(buttonPanel, gbc);

        // ---------------- DEMO INFORMATION ----------------
        JLabel demoLabel = new JLabel(
                "Demo Login: admin / admin123",
                SwingConstants.CENTER
        );

        demoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        demoLabel.setForeground(Color.GRAY);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(demoLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // ---------------- BUTTON ACTIONS ----------------

        // Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Clear button
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameField.setText("");
                passwordField.setText("");
                usernameField.requestFocus();
            }
        });

        // Press ENTER to login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    // Login validation

    private void login() {

        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            usernameField.requestFocus();
            return;
        }

        if (passwordChars.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            passwordField.requestFocus();
            return;
        }

        String password = new String(passwordChars);

        try {
            LoginService loginService = new LoginService();

            LoginService.AuthenticatedUser user =
                    loginService.authenticate(username, password);

            if (user != null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful! Welcome, " + user.getName(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
                passwordField.requestFocus();
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to the database. Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }


    // Main method for testing LoginFrame independently
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
