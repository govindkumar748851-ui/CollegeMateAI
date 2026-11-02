
package com.collegemate.frontend;

import com.collegemate.backend.service.ChatService;
import com.collegemate.backend.service.ChatHistoryService;
import com.collegemate.backend.service.AuthSession;
import java.util.UUID;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class ChatPanel extends JPanel {

    private final Color primary = new Color(48, 91, 153);
    private final Color background = new Color(245, 247, 251);
    private final Color botBubble = Color.WHITE;

    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private JTextField messageInput;
    private JButton sendButton;

    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;
    private final String conversationId;
    private final String userId;

    public ChatPanel() {

        chatService = new ChatService();
        chatHistoryService = new ChatHistoryService();
        if (AuthSession.getCurrentUser() == null) {
            throw new IllegalStateException("User must be logged in to use chat history.");
        }
        userId = AuthSession.getCurrentUser().getUserId();
        conversationId = UUID.randomUUID().toString();

        setLayout(new BorderLayout());
        setBackground(background);

        add(createChatHeader(), BorderLayout.NORTH);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(
                new BoxLayout(messagesPanel, BoxLayout.Y_AXIS)
        );

        messagesPanel.setBackground(background);
        messagesPanel.setBorder(
                new EmptyBorder(20, 25, 20, 25)
        );

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(null);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(background);

        add(scrollPane, BorderLayout.CENTER);

        add(createInputPanel(), BorderLayout.SOUTH);

        addBotMessage(
                "Hello! Welcome to CollegeMate AI. "
                + "How can I help you today?"
        );

        addSuggestedQuestions();
    }

    // CHAT HEADER

    private JPanel createChatHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        header.setBorder(
                new EmptyBorder(18, 25, 18, 25)
        );

        JLabel title = new JLabel(
                "CollegeMate AI Assistant"
        );

        title.setFont(
                new Font("SansSerif", Font.BOLD, 20)
        );

        JLabel status = new JLabel("● Online");
        status.setForeground(new Color(40, 160, 95));

        status.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        JPanel details = new JPanel();
        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );

        details.setOpaque(false);

        details.add(title);
        details.add(Box.createVerticalStrut(5));
        details.add(status);

        header.add(details, BorderLayout.WEST);

        return header;
    }

    // INPUT PANEL

    private JPanel createInputPanel() {

        JPanel container = new JPanel(
                new BorderLayout(12, 0)
        );

        container.setBackground(Color.WHITE);
        container.setBorder(
                new EmptyBorder(15, 20, 15, 20)
        );

        messageInput = new JTextField();

        messageInput.setFont(
                new Font("SansSerif", Font.PLAIN, 15)
        );

        messageInput.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        messageInput.putClientProperty(
                "JTextField.placeholderText",
                "Ask anything about your college..."
        );

        sendButton = new JButton("Send");

        sendButton.setBackground(primary);
        sendButton.setForeground(Color.WHITE);

        sendButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);

        sendButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        sendButton.addActionListener(e -> sendMessage());

        messageInput.addActionListener(e -> sendMessage());

        container.add(messageInput, BorderLayout.CENTER);
        container.add(sendButton, BorderLayout.EAST);

        return container;
    }

    // SUGGESTED QUESTIONS

    private void addSuggestedQuestions() {

        JLabel heading = new JLabel("Try asking:");

        heading.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        heading.setForeground(
                new Color(90, 100, 115)
        );

        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        messagesPanel.add(heading);
        messagesPanel.add(Box.createVerticalStrut(10));

        String[] questions = {
                "What courses are available?",
                "How can I apply for admission?",
                "Tell me about placements",
                "What are the college fees?"
        };

        for (String question : questions) {

            JButton suggestion = new JButton(question);

            suggestion.setFont(
                    new Font("SansSerif", Font.PLAIN, 12)
            );

            suggestion.setForeground(primary);
            suggestion.setBackground(Color.WHITE);
            suggestion.setFocusPainted(false);

            suggestion.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            suggestion.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            suggestion.addActionListener(e -> {
                messageInput.setText(question);
                sendMessage();
            });

            messagesPanel.add(suggestion);
            messagesPanel.add(Box.createVerticalStrut(8));
        }

        messagesPanel.add(Box.createVerticalStrut(15));
    }

    // SEND MESSAGE

    private void sendMessage() {

        String message = messageInput.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        addUserMessage(message);

        messageInput.setText("");

        messageInput.setEnabled(false);
        sendButton.setEnabled(false);

        addBotMessage("Thinking...");

        SwingWorker<String, Void> worker =
                new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() {

                try {
                    chatHistoryService.saveMessage(
                            userId, conversationId, "USER", message
                    );
                } catch (Exception historyError) {
                    historyError.printStackTrace();
                }

                String response = chatService.getResponse(message);

                try {
                    chatHistoryService.saveMessage(
                            userId, conversationId, "ASSISTANT", response
                    );
                } catch (Exception historyError) {
                    historyError.printStackTrace();
                }

                return response;
            }

            @Override
            protected void done() {

                try {

                    String response = getResponse();

                    replaceThinkingMessage(response);

                } catch (Exception e) {

                    replaceThinkingMessage(
                            "Sorry, something went wrong. "
                            + "Please try again."
                    );

                    e.printStackTrace();

                } finally {

                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                    messageInput.requestFocusInWindow();
                }
            }

            private String getResponse() throws Exception {
                return get();
            }
        };

        worker.execute();
    }

    // MESSAGE TYPES

    private void addUserMessage(String message) {
        addMessageBubble(message, true);
    }

    private void addBotMessage(String message) {
        addMessageBubble(message, false);
    }

    // REPLACE THINKING MESSAGE

    private void replaceThinkingMessage(String response) {

        int lastIndex = messagesPanel.getComponentCount() - 1;

        if (lastIndex >= 0) {

            messagesPanel.remove(lastIndex);

            if (lastIndex - 1 >= 0) {
                messagesPanel.remove(lastIndex - 1);
            }
        }

        addBotMessage(response);
    }

    // MESSAGE BUBBLE

    private void addMessageBubble(
            String message,
            boolean isUser
    ) {

        JPanel row = new JPanel(
                new FlowLayout(
                        isUser
                                ? FlowLayout.RIGHT
                                : FlowLayout.LEFT,
                        5,
                        5
                )
        );

        row.setBackground(background);

        row.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JTextArea bubbleText = new JTextArea(message);

        bubbleText.setEditable(false);
        bubbleText.setFocusable(false);

        bubbleText.setLineWrap(true);
        bubbleText.setWrapStyleWord(true);

        bubbleText.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        bubbleText.setBorder(
                new EmptyBorder(12, 14, 12, 14)
        );

        bubbleText.setBackground(
                isUser ? primary : botBubble
        );

        bubbleText.setForeground(
                isUser
                        ? Color.WHITE
                        : new Color(40, 45, 55)
        );

        int width = 380;

        bubbleText.setSize(
                width,
                Short.MAX_VALUE
        );

        Dimension textSize = bubbleText.getPreferredSize();

        bubbleText.setPreferredSize(
                new Dimension(width, textSize.height)
        );

        bubbleText.setMinimumSize(
                new Dimension(width, textSize.height)
        );

        bubbleText.setMaximumSize(
                new Dimension(width, textSize.height)
        );

        row.add(bubbleText);

        messagesPanel.add(row);

        messagesPanel.add(
                Box.createVerticalStrut(10)
        );

        messagesPanel.revalidate();
        messagesPanel.repaint();

        SwingUtilities.invokeLater(() -> {

            JScrollBar verticalBar =
                    scrollPane.getVerticalScrollBar();

            verticalBar.setValue(
                    verticalBar.getMaximum()
            );
        });
    }
}
