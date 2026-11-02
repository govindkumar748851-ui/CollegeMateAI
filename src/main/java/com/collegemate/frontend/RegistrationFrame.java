package com.collegemate.frontend;

import com.collegemate.backend.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistrationFrame extends JFrame {
    private final AuthService authService = new AuthService();

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleBox;

    private final Color navy = new Color(22, 34, 58);
    private final Color blue = new Color(48, 91, 153);

    public RegistrationFrame() {
        setTitle("CollegeMate AI - Create Account");
        setSize(470, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(new Color(245, 247, 251));
        root.setBorder(new EmptyBorder(30, 45, 30, 45));

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(navy);

        JLabel subtitle = new JLabel(
            "Join your college workspace",
            SwingConstants.CENTER
        );
        subtitle.setForeground(Color.GRAY);

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 7));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        nameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        roleBox = new JComboBox<>(new String[]{"STUDENT", "STAFF"});

        addField(form, "Full Name", nameField);
        addField(form, "Email Address", emailField);
        addField(form, "Password (minimum 8 characters)", passwordField);
        addField(form, "Confirm Password", confirmPasswordField);
        addField(form, "Account Type", roleBox);

        JButton registerButton = new JButton("Create Account");
        styleButton(registerButton);
        registerButton.addActionListener(e -> register());

        JButton backButton = new JButton("Back to Sign In");
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());

        form.add(Box.createVerticalStrut(10));
        form.add(registerButton);
        form.add(Box.createVerticalStrut(10));
        form.add(backButton);

        root.add(heading, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void addField(JPanel panel, String label, JComponent field) {
        JLabel title = new JLabel(label);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(13));
    }

    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try {
            authService.registerPublicUser(name, email, password, role);

            if ("STAFF".equals(role)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Staff registration submitted. Please wait for Admin approval."
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Student account created successfully. You can now sign in."
                );
            }

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Registration failed: " + e.getMessage()
            );
        }
    }
}
