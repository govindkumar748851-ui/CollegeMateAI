package com.collegemate.frontend;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.collegemate.backend.service.ChatHistoryService;
import com.collegemate.backend.service.AuthSession;
import com.collegemate.model.ChatMessage;

public class ChatHistoryPanel extends JPanel {

    private final ChatHistoryService service = new ChatHistoryService();
    private final String userId = AuthSession.getCurrentUser() == null ? null : AuthSession.getCurrentUser().getUserId();

    private final Color primary = new Color(48, 91, 153);
    private final Color dark = new Color(22, 34, 58);

    private DefaultListModel<String> conversationModel;
    private JList<String> conversationList;
    private JTextArea conversationArea;
    private JTextField searchField;

    public ChatHistoryPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(22, 25, 22, 25));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadConversations();
    }

    private JPanel createHeader() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Chat History");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(dark);

        JLabel subtitle = new JLabel("View and manage previous AI conversations.");

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);

        return panel;
    }

    private JPanel createContent() {

        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(Color.WHITE);
        left.setPreferredSize(new Dimension(280, 0));
        left.setBorder(new EmptyBorder(15, 15, 15, 15));

        searchField = new JTextField();
        JButton searchButton = createButton("Search");

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        conversationModel = new DefaultListModel<>();
        conversationList = new JList<>(conversationModel);
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedConversation();
            }
        });

        searchButton.addActionListener(e -> searchMessages());

        JButton refresh = createButton("Refresh");
        refresh.addActionListener(e -> loadConversations());

        JButton delete = createButton("Delete Conversation");
        delete.addActionListener(e -> deleteSelectedConversation());

        JPanel leftBottom = new JPanel(new GridLayout(2, 1, 0, 8));
        leftBottom.add(refresh);
        leftBottom.add(delete);

        left.add(searchPanel, BorderLayout.NORTH);
        left.add(new JScrollPane(conversationList), BorderLayout.CENTER);
        left.add(leftBottom, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Conversation Messages");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));

        conversationArea = new JTextArea();
        conversationArea.setEditable(false);
        conversationArea.setLineWrap(true);
        conversationArea.setWrapStyleWord(true);
        conversationArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        right.add(heading, BorderLayout.NORTH);
        right.add(new JScrollPane(conversationArea), BorderLayout.CENTER);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(primary);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private void loadConversations() {

        try {
            conversationModel.clear();

            for (String id : service.getConversationIds(userId)) {
                conversationModel.addElement(id);
            }

            if (conversationModel.isEmpty()) {
                conversationArea.setText(
                        "No saved conversations yet. Start chatting with CollegeMate AI."
                );
            } else {
                conversationArea.setText("Loading conversation...");
                conversationList.setSelectedIndex(0);
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadSelectedConversation() {

        String id = conversationList.getSelectedValue();

        if (id == null) return;

        try {
            List<ChatMessage> messages = service.getConversation(userId, id);

            StringBuilder output = new StringBuilder();

            for (ChatMessage message : messages) {
                output.append(message.getRole())
                        .append("\n")
                        .append(message.getMessage())
                        .append("\n\n-------------------------\n\n");
            }

            conversationArea.setText(output.toString());
            conversationArea.setCaretPosition(0);

        } catch (Exception e) {
            showError(e);
        }
    }

    private void searchMessages() {

        try {
            List<ChatMessage> results =
                    service.searchMessages(userId, searchField.getText());

            conversationModel.clear();

            for (ChatMessage message : results) {
                if (!conversationModel.contains(message.getConversationId())) {
                    conversationModel.addElement(message.getConversationId());
                }
            }

            if (!conversationModel.isEmpty()) {
                conversationList.setSelectedIndex(0);
            } else {
                conversationArea.setText("No matching messages found.");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteSelectedConversation() {

        String id = conversationList.getSelectedValue();

        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a conversation first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete this complete conversation?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.deleteConversation(userId, id);
            conversationArea.setText("");
            loadConversations();
            JOptionPane.showMessageDialog(this, "Conversation deleted.");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage() == null ? "Something went wrong." : e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}