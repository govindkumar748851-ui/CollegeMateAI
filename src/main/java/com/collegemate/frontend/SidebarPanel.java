package com.collegemate.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import com.collegemate.backend.service.AuthSession;
import com.collegemate.model.AppUser;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SidebarPanel extends JPanel {

    private final Color sidebarColor = new Color(22, 34, 58);
    private final Color activeColor = new Color(48, 91, 153);

    private JButton activeButton;

    private final Map<String, JButton> menuButtons = new HashMap<>();

    public SidebarPanel(Consumer<String> navigation) {

        setPreferredSize(new Dimension(245, 750));
        setBackground(sidebarColor);
        setLayout(new BorderLayout());

        JPanel logoPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 20, 25)
        );
        logoPanel.setBackground(sidebarColor);

        JLabel logo = new JLabel("CollegeMate AI");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 21));
        logoPanel.add(logo);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(sidebarColor);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        String[] menus = {
                "Dashboard",
                "AI Chat",
                "College FAQs",
                "Courses",
                "Admissions",
                "Placements",
                "Student Placement Tracking",
                "Chat History",
                "User Management", "Settings"
        };

        for (String menu : menus) {

            JButton button = new JButton(menu);

            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(sidebarColor);
            button.setHorizontalAlignment(SwingConstants.LEFT);

            button.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 48)
            );

            button.setPreferredSize(new Dimension(225, 48));

            button.setBorder(
                    BorderFactory.createEmptyBorder(0, 18, 0, 8)
            );

            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            menuButtons.put(menu, button);

            button.addActionListener(e -> {
                setActiveMenu(menu);
                navigation.accept(menu);
            });

            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(5));
        }

        AppUser currentUser = AuthSession.getCurrentUser();

        boolean isAdmin = currentUser != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole());

        JButton userManagementButton = menuButtons.get("User Management");

        if (userManagementButton != null) {
            userManagementButton.setVisible(isAdmin);
        }
        setActiveMenu("Dashboard");

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 20, 20)
        );
        bottomPanel.setBackground(sidebarColor);

        JLabel footer = new JLabel("CollegeMate AI v1.0");
        footer.setForeground(new Color(170, 185, 205));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));

        bottomPanel.add(footer);

        add(logoPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setActiveMenu(String menu) {

        JButton button = menuButtons.get(menu);

        if (button == null) {
            return;
        }

        if (activeButton != null) {
            activeButton.setBackground(sidebarColor);
        }

        activeButton = button;
        activeButton.setBackground(activeColor);
    }
}