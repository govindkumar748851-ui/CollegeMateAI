package com.collegemate.frontend;

import com.collegemate.backend.service.AuthService;
import com.collegemate.backend.service.AuthSession;
import com.collegemate.backend.service.UserSessionService;
import com.collegemate.model.AppUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthService();
    private JTextField emailField;
    private JPasswordField passwordField;

    private final Color navy = new Color(22, 34, 58);
    private final Color blue = new Color(48, 91, 153);

    public LoginFrame() {
        setTitle("CollegeMate AI - Login");
        setSize(460, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 251));
        root.setBorder(new EmptyBorder(35, 45, 35, 45));

        JLabel title = new JLabel("CollegeMate AI", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(navy);

        JLabel subtitle = new JLabel(
            "Sign in to your college workspace",
            SwingConstants.CENTER
        );
        subtitle.setForeground(Color.GRAY);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 8));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(35, 0, 0, 0));

        JLabel emailLabel = new JLabel("Email address");
        emailField = new JTextField();
        styleField(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        styleField(passwordField);

        JButton loginButton = new JButton("Sign In");
        styleButton(loginButton);
        loginButton.addActionListener(e -> login());

        JButton registerButton = new JButton("Create New Account");
        styleLinkButton(registerButton);
        registerButton.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
        });

        JButton createAdminButton = new JButton("Create First Admin Account");
        styleLinkButton(createAdminButton);
        createAdminButton.addActionListener(e -> createFirstAdmin());

        form.add(emailLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(emailField);
        form.add(Box.createVerticalStrut(20));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(25));
        form.add(loginButton);
        form.add(Box.createVerticalStrut(12));
        form.add(registerButton);
        form.add(Box.createVerticalStrut(5));
        form.add(createAdminButton);

        root.add(heading, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);

        createAdminButton.setVisible(!hasUsersSafely());
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 232)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    private void styleLinkButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setForeground(blue);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private boolean hasUsersSafely() {
        try {
            return authService.hasUsers();
        } catch (Exception e) {
            return true;
        }
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter email and password.");
            return;
        }

        try {
            AppUser user = authService.login(email, password);
            if (user == null) {
                JOptionPane.showMessageDialog(
                    this, "Invalid credentials or inactive account."
                );
                return;
            }

            AuthSession.login(user);
            UserSessionService.startSession(user.getUserId());
            openDashboard();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createFirstAdmin() {
        JTextField nameField = new JTextField();
        JTextField adminEmailField = new JTextField();
        JPasswordField adminPasswordField = new JPasswordField();

        Object[] fields = {
            "Admin name:", nameField,
            "Admin email:", adminEmailField,
            "Password (minimum 8 characters):", adminPasswordField
        };

        int choice = JOptionPane.showConfirmDialog(
            this, fields, "Create First Admin", JOptionPane.OK_CANCEL_OPTION
        );

        if (choice != JOptionPane.OK_OPTION) return;

        try {
            authService.createFirstAdmin(
                nameField.getText(),
                adminEmailField.getText(),
                new String(adminPasswordField.getPassword())
            );

            JOptionPane.showMessageDialog(
                this, "Admin account created. You can now sign in."
            );
            buildUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this, "Could not create admin: " + e.getMessage()
            );
        }
    }

    private void openDashboard() {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
        });
        dispose();
    }
}
