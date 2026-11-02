package com.collegemate.frontend;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.collegemate.backend.service.FAQService;
import com.collegemate.backend.service.AuthSession;
import com.collegemate.backend.service.UserSessionService;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.collegemate.model.AppUser;
import com.collegemate.model.FAQ;

public class DashboardFrame extends JFrame {

    private final Color background = new Color(245, 247, 251);
    private final Color primary = new Color(48, 91, 153);
    private final Color dark = new Color(22, 34, 58);
    private final Color muted = new Color(110, 120, 135);

    private JPanel mainContent;
    private JLabel pageTitle;
    private JLabel profileLabel;
    private CardLayout cardLayout;
    private SidebarPanel sidebar;

    private JLabel faqCountValue;
    private JLabel categoryCountValue;
    private DefaultTableModel recentTableModel;

    private final FAQService faqService = new FAQService();

    public DashboardFrame() {

        setTitle("CollegeMate AI | College ERP");
        setSize(1250, 800);
        setMinimumSize(new Dimension(1050, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                UserSessionService.endCurrentSession();
            }
        });
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidebar = new SidebarPanel(this::navigate);
        add(sidebar, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(background);
        rightPanel.add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(background);

        mainContent.add(createDashboard(), "Dashboard");
        mainContent.add(new ChatPanel(), "AI Chat");
        mainContent.add(new FAQPanel(), "College FAQs");

        mainContent.add(new CoursePanel(), "Courses");
        mainContent.add(new AdmissionPanel(), "Admissions");
        mainContent.add(new PlacementPanel(), "Placements");
        mainContent.add(new ChatHistoryPanel(), "Chat History");
        mainContent.add(new StudentPlacementPanel(), "Student Placement Tracking");
        mainContent.add(new SettingsPanel(), "Settings");
        mainContent.add(new UserManagementPanel(), "User Management");

        rightPanel.add(mainContent, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        navigate("Dashboard");
        loadDashboardData();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 28, 18, 28));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 23));

        profileLabel = new JLabel(getProfileText());
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setForeground(muted);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> logout());

        JPanel profileActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        profileActions.setOpaque(false);
        profileActions.add(profileLabel);
        profileActions.add(logoutButton);

        header.add(pageTitle, BorderLayout.WEST);
        header.add(profileActions, BorderLayout.EAST);

        return header;
    }
    private String getProfileText() {
        AppUser user = AuthSession.getCurrentUser();

        if (user == null) {
            return "Not signed in | CollegeMate AI";
        }

        return user.getName() + "  |  " + user.getRole();
    }

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            LoginFrame loginFrame = new LoginFrame();

            UserSessionService.endCurrentSession();
            AuthSession.logout();
            loginFrame.setVisible(true);
            dispose();

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                "Unable to open Login screen because the database is unavailable. Please check your internet and MongoDB Atlas connection, then try again.",
                "Login Startup Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private JPanel createDashboard() {

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(background);
        content.setBorder(new EmptyBorder(25, 28, 25, 28));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // Welcome section

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome to CollegeMate AI");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 27));
        welcome.setForeground(dark);

        JLabel subtitle = new JLabel(
            "Your college information and enquiry management workspace."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(muted);

        welcomeText.add(welcome);
        welcomeText.add(Box.createVerticalStrut(7));
        welcomeText.add(subtitle);

        JLabel dateLabel = new JLabel(
            LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy")
            )
        );

        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dateLabel.setForeground(primary);

        welcomePanel.add(welcomeText, BorderLayout.WEST);
        welcomePanel.add(dateLabel, BorderLayout.EAST);

        body.add(welcomePanel);

        // Statistics cards

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

        JPanel faqCard = createStatCard(
            "TOTAL FAQs", "Loading...", "Questions stored in MongoDB"
        );

        faqCountValue = (JLabel) faqCard.getClientProperty("valueLabel");

        JPanel categoryCard = createStatCard(
            "FAQ CATEGORIES", "Loading...", "Unique FAQ categories"
        );

        categoryCountValue =
            (JLabel) categoryCard.getClientProperty("valueLabel");

        JPanel aiCard = createStatCard(
            "AI ASSISTANT", "Ollama", "Local AI model"
        );

        stats.add(faqCard);
        stats.add(categoryCard);
        stats.add(aiCard);

        body.add(stats);
        body.add(Box.createVerticalStrut(25));

        // Quick access

        JLabel quickHeading = new JLabel("Quick Access");
        quickHeading.setFont(new Font("SansSerif", Font.BOLD, 19));
        quickHeading.setForeground(dark);

        body.add(quickHeading);
        body.add(Box.createVerticalStrut(13));

        JPanel quickAccess = new JPanel(new GridLayout(1, 3, 16, 0));
        quickAccess.setOpaque(false);
        quickAccess.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        quickAccess.add(createQuickCard(
            "Course Management", "Manage college courses", "Courses"
        ));

        quickAccess.add(createQuickCard(
            "Admissions", "Admission information", "Admissions"
        ));

        quickAccess.add(createQuickCard(
            "Placements", "Placement information", "Placements"
        ));

        body.add(quickAccess);
        body.add(Box.createVerticalStrut(25));

        // Recent FAQs

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel recentTitle = new JLabel("Recently Available FAQs");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        recentTitle.setForeground(dark);

        JButton viewAllButton = new JButton("View All FAQs");
        styleButton(viewAllButton);

        viewAllButton.addActionListener(
            e -> navigate("College FAQs")
        );

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(viewAllButton, BorderLayout.EAST);

        body.add(recentHeader);
        body.add(Box.createVerticalStrut(12));

        recentTableModel = new DefaultTableModel(
            new String[]{"Question", "Category"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable recentTable = new JTable(recentTableModel);
        recentTable.setRowHeight(34);
        recentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        recentTable.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        JScrollPane tableScroll = new JScrollPane(recentTable);
        tableScroll.setPreferredSize(new Dimension(500, 190));
        tableScroll.setBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 238))
        );

        body.add(tableScroll);

        JScrollPane pageScroll = new JScrollPane(body);
        pageScroll.setBorder(null);
        pageScroll.getViewport().setBackground(background);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);

        content.add(pageScroll, BorderLayout.CENTER);

        return content;
    }

    private JPanel createStatCard(
        String title,
        String value,
        String description
    ) {

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 240)),
                new EmptyBorder(16, 18, 16, 18)
            )
        );

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLabel.setForeground(muted);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        valueLabel.setForeground(primary);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setForeground(muted);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);

        card.putClientProperty("valueLabel", valueLabel);

        return card;
    }

    private JPanel createQuickCard(
        String title,
        String description,
        String destination
    ) {

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 240)),
                new EmptyBorder(15, 16, 15, 16)
            )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(dark);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setForeground(muted);

        JButton openButton = new JButton("Open");
        styleButton(openButton);

        openButton.addActionListener(
            e -> navigate(destination)
        );

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionLabel, BorderLayout.CENTER);
        card.add(openButton, BorderLayout.EAST);

        return card;
    }

    private void styleButton(JButton button) {

        button.setBackground(primary);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadDashboardData() {

        faqCountValue.setText("Loading...");
        categoryCountValue.setText("Loading...");

        new SwingWorker<Object[], Void>() {

            @Override
            protected Object[] doInBackground() throws Exception {

                List<FAQ> faqs = faqService.getAllFAQs();

                Set<String> categories = new HashSet<>();

                for (FAQ faq : faqs) {
                    if (faq.getCategory() != null
                        && !faq.getCategory().isBlank()) {
                        categories.add(faq.getCategory().trim());
                    }
                }

                return new Object[]{
                    faqs.size(),
                    categories.size(),
                    faqs
                };
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void done() {

                try {

                    Object[] data = get();

                    faqCountValue.setText(data[0].toString());
                    categoryCountValue.setText(data[1].toString());

                    List<FAQ> faqs = (List<FAQ>) data[2];

                    recentTableModel.setRowCount(0);

                    int start = Math.max(0, faqs.size() - 5);

                    for (int i = faqs.size() - 1; i >= start; i--) {

                        FAQ faq = faqs.get(i);

                        recentTableModel.addRow(new Object[]{
                            faq.getQuestion(),
                            faq.getCategory()
                        });
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    faqCountValue.setText("Error");
                    categoryCountValue.setText("Error");
                }
            }

        }.execute();
    }

    private JPanel createPlaceholder(String name) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel(name);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(dark);

        JLabel message = new JLabel(
            name + " management module is under development."
        );

        message.setFont(new Font("SansSerif", Font.PLAIN, 15));
        message.setForeground(muted);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(title);
        center.add(Box.createVerticalStrut(12));
        center.add(message);

        panel.add(center, BorderLayout.NORTH);

        return panel;
    }

    private void navigate(String page) {

        AppUser currentUser = AuthSession.getCurrentUser();

        if ("User Management".equals(page)
                && (currentUser == null
                || !"ADMIN".equalsIgnoreCase(currentUser.getRole()))) {

            JOptionPane.showMessageDialog(
                this,
                "Access denied. Only Admin users can open User Management.",
                "Permission Denied",
                JOptionPane.WARNING_MESSAGE
            );

            pageTitle.setText("Dashboard");
            cardLayout.show(mainContent, "Dashboard");
            sidebar.setActiveMenu("Dashboard");
            return;
        }

        pageTitle.setText(page);
        cardLayout.show(mainContent, page);
        sidebar.setActiveMenu(page);
    }
}
