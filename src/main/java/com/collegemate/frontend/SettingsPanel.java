package com.collegemate.frontend;

import com.collegemate.database.MongoDBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SettingsPanel extends JPanel {

    private JLabel javaStatus;
    private JLabel mongoStatus;
    private JLabel ollamaStatus;
    private JLabel apiStatus;

    private final Color navy = new Color(22, 34, 58);
    private final Color blue = new Color(48, 91, 153);

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        refreshStatuses();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 22, 0));

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Application configuration and system status");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitle);

        JButton refreshButton = new JButton("Refresh Status");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(blue);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> refreshStatuses());

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(refreshButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createSectionTitle("System Information"));

        JPanel info = new JPanel(new GridLayout(2, 2, 15, 15));
        info.setOpaque(false);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        info.add(createInfoCard("Application", "CollegeMate AI"));
        info.add(createInfoCard("Technology", "Java Swing"));
        info.add(createInfoCard("Java Version", System.getProperty("java.version")));
        info.add(createInfoCard("AI Model", "llama3.2:3b"));

        content.add(info);
        content.add(Box.createVerticalStrut(28));
        content.add(createSectionTitle("Connection Status"));

        JPanel status = new JPanel(new GridLayout(2, 2, 15, 15));
        status.setOpaque(false);
        status.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        javaStatus = new JLabel("Checking...");
        mongoStatus = new JLabel("Checking...");
        ollamaStatus = new JLabel("Checking...");
        apiStatus = new JLabel("Checking...");

        status.add(createStatusCard("Java Runtime", javaStatus));
        status.add(createStatusCard("MongoDB Atlas", mongoStatus));
        status.add(createStatusCard("Ollama AI", ollamaStatus));
        status.add(createStatusCard("Backend API", apiStatus));

        content.add(status);
        content.add(Box.createVerticalStrut(25));

        JLabel note = new JLabel(
            "<html><b>Note:</b> Connection status is checked when this page opens or when you click Refresh Status.</html>"
        );
        note.setFont(new Font("SansSerif", Font.PLAIN, 12));
        note.setForeground(Color.GRAY);
        content.add(note);

        return content;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(navy);
        label.setBorder(new EmptyBorder(0, 0, 14, 0));
        return label;
    }

    private JPanel createInfoCard(String heading, String value) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 238)),
            new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel(heading);
        title.setForeground(Color.GRAY);
        title.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel detail = new JLabel(value);
        detail.setForeground(navy);
        detail.setFont(new Font("SansSerif", Font.BOLD, 16));

        card.add(title, BorderLayout.NORTH);
        card.add(detail, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatusCard(String heading, JLabel statusLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 238)),
            new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel(heading);
        title.setForeground(Color.GRAY);
        title.setFont(new Font("SansSerif", Font.PLAIN, 12));

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        statusLabel.setForeground(new Color(180, 130, 30));

        card.add(title, BorderLayout.NORTH);
        card.add(statusLabel, BorderLayout.CENTER);
        return card;
    }

    private void refreshStatuses() {
        javaStatus.setText("Running");
        javaStatus.setForeground(new Color(30, 145, 85));

        mongoStatus.setText("Checking...");
        ollamaStatus.setText("Checking...");
        apiStatus.setText("Checking...");

        SwingWorker<String[], Void> worker = new SwingWorker<>() {
            @Override
            protected String[] doInBackground() {
                String[] results = new String[3];

                try {
                    MongoDBConnection.getCollection("faqs").countDocuments();
                    results[0] = "Connected";
                } catch (Exception e) {
                    results[0] = "Disconnected";
                }

                try {
                    HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .build();

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/tags"))
                        .timeout(Duration.ofSeconds(4))
                        .GET()
                        .build();

                    HttpResponse<String> response = client.send(
                        request, HttpResponse.BodyHandlers.ofString()
                    );

                    results[1] = response.statusCode() == 200
                        ? "Connected" : "Unavailable";
                } catch (Exception e) {
                    results[1] = "Unavailable";
                }

                try {
                    HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .build();

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/health"))
                        .timeout(Duration.ofSeconds(4))
                        .GET()
                        .build();

                    HttpResponse<String> response = client.send(
                        request, HttpResponse.BodyHandlers.ofString()
                    );

                    results[2] = response.statusCode() == 200
                        ? "Running" : "Unavailable";
                } catch (Exception e) {
                    results[2] = "Unavailable";
                }

                return results;
            }

            @Override
            protected void done() {
                try {
                    String[] results = get();
                    updateStatus(mongoStatus, results[0]);
                    updateStatus(ollamaStatus, results[1]);
                    updateStatus(apiStatus, results[2]);
                } catch (Exception e) {
                    updateStatus(mongoStatus, "Check failed");
                    updateStatus(ollamaStatus, "Check failed");
                    updateStatus(apiStatus, "Check failed");
                }
            }
        };

        worker.execute();
    }

    private void updateStatus(JLabel label, String value) {
        label.setText(value);

        if ("Connected".equals(value) || "Running".equals(value)) {
            label.setForeground(new Color(30, 145, 85));
        } else {
            label.setForeground(new Color(190, 65, 65));
        }
    }
}