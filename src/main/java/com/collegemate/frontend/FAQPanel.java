
package com.collegemate.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.collegemate.backend.service.FAQService;
import com.collegemate.model.FAQ;

public class FAQPanel extends JPanel {

    private final Color primary = new Color(48, 91, 153);
    private final Color background = new Color(245, 247, 251);

    private final FAQService faqService = new FAQService();

    private JTable faqTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public FAQPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(background);
        setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadFAQs();
    }

    // TOP PANEL

    private JPanel createTopPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setOpaque(false);

        JLabel heading = new JLabel("College FAQ Management");
        heading.setFont(
                new Font("SansSerif", Font.BOLD, 23)
        );

        JLabel subtitle = new JLabel(
                "Manage college questions and answers stored in MongoDB."
        );

        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);

        titlePanel.add(heading);
        titlePanel.add(subtitle);

        searchField = new JTextField();
        searchField.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        searchField.setToolTipText(
                "Search by question or category"
        );

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    public void insertUpdate(DocumentEvent e) {
                        searchFAQs();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        searchFAQs();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        searchFAQs();
                    }
                }
        );

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);

        searchPanel.add(
                new JLabel("Search: "),
                BorderLayout.WEST
        );

        searchPanel.add(searchField, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.SOUTH);

        return panel;
    }

    // FAQ TABLE

    private JScrollPane createTable() {

        String[] columns = {
                "Question",
                "Answer",
                "Category"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        faqTable = new JTable(tableModel);

        faqTable.setRowHeight(35);
        faqTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        faqTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        faqTable.getTableHeader().setBackground(
                new Color(230, 235, 243)
        );

        faqTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        faqTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_LAST_COLUMN
        );

        JScrollPane scrollPane = new JScrollPane(faqTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 232)
                )
        );

        return scrollPane;
    }

    // BOTTOM BUTTONS

    private JPanel createBottomPanel() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );

        panel.setOpaque(false);

        JButton addButton = createButton("Add FAQ");
        JButton editButton = createButton("Edit Selected");
        JButton deleteButton = createButton("Delete Selected");
        JButton refreshButton = createButton("Refresh");

        addButton.addActionListener(e -> addFAQ());
        editButton.addActionListener(e -> editFAQ());
        deleteButton.addActionListener(e -> deleteFAQ());
        refreshButton.addActionListener(e -> refreshFAQs());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

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

    // LOAD FAQs

    private void loadFAQs() {

        try {

            List<FAQ> faqs = faqService.getAllFAQs();

            tableModel.setRowCount(0);

            for (FAQ faq : faqs) {

                tableModel.addRow(
                        new Object[] {
                                faq.getQuestion(),
                                faq.getAnswer(),
                                faq.getCategory()
                        }
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load FAQs from MongoDB.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // SEARCH FAQs

    private void searchFAQs() {

        String keyword = searchField.getText().trim();

        try {

            List<FAQ> faqs = faqService.searchFAQs(keyword);

            tableModel.setRowCount(0);

            for (FAQ faq : faqs) {

                tableModel.addRow(
                        new Object[] {
                                faq.getQuestion(),
                                faq.getAnswer(),
                                faq.getCategory()
                        }
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ADD FAQ

    private void addFAQ() {

        JTextField questionField = new JTextField();
        JTextArea answerArea = new JTextArea(5, 25);
        JTextField categoryField = new JTextField();

        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        JPanel form = createFAQForm(
                questionField,
                answerArea,
                categoryField
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add New FAQ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            faqService.addFAQ(
                    questionField.getText(),
                    answerArea.getText(),
                    categoryField.getText()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "FAQ added successfully!"
            );

            refreshFAQs();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Unable to Add FAQ",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // EDIT FAQ

    private void editFAQ() {

        int selectedRow = faqTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an FAQ to edit."
            );

            return;
        }

        String oldQuestion = tableModel.getValueAt(
                selectedRow, 0
        ).toString();

        JTextField questionField = new JTextField(
                oldQuestion
        );

        JTextArea answerArea = new JTextArea(
                tableModel.getValueAt(selectedRow, 1).toString(),
                5,
                25
        );

        JTextField categoryField = new JTextField(
                tableModel.getValueAt(selectedRow, 2).toString()
        );

        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        JPanel form = createFAQForm(
                questionField,
                answerArea,
                categoryField
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit FAQ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            boolean updated = faqService.updateFAQ(
                    oldQuestion,
                    questionField.getText(),
                    answerArea.getText(),
                    categoryField.getText()
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "FAQ updated successfully!"
                );

                refreshFAQs();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "FAQ not found."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // DELETE FAQ

    private void deleteFAQ() {

        int selectedRow = faqTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an FAQ to delete."
            );

            return;
        }

        String question = tableModel.getValueAt(
                selectedRow, 0
        ).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this FAQ?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted = faqService.deleteFAQ(question);

            if (deleted) {

                JOptionPane.showMessageDialog(
                        this,
                        "FAQ deleted successfully!"
                );

                refreshFAQs();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "FAQ not found."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete FAQ: " + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // FAQ FORM

    private JPanel createFAQForm(
            JTextField questionField,
            JTextArea answerArea,
            JTextField categoryField
    ) {

        JPanel form = new JPanel(new GridLayout(3, 1, 5, 10));

        JPanel questionPanel = new JPanel(new BorderLayout(5, 5));
        questionPanel.add(
                new JLabel("Question:"),
                BorderLayout.NORTH
        );
        questionPanel.add(questionField, BorderLayout.CENTER);

        JPanel answerPanel = new JPanel(new BorderLayout(5, 5));
        answerPanel.add(
                new JLabel("Answer:"),
                BorderLayout.NORTH
        );
        answerPanel.add(
                new JScrollPane(answerArea),
                BorderLayout.CENTER
        );

        JPanel categoryPanel = new JPanel(new BorderLayout(5, 5));
        categoryPanel.add(
                new JLabel("Category:"),
                BorderLayout.NORTH
        );
        categoryPanel.add(categoryField, BorderLayout.CENTER);

        form.add(questionPanel);
        form.add(answerPanel);
        form.add(categoryPanel);

        return form;
    }

    // REFRESH

    private void refreshFAQs() {

        searchField.setText("");
        loadFAQs();
    }
}
