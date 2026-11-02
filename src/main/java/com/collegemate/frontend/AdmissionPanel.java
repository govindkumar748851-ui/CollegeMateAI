package com.collegemate.frontend;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.collegemate.backend.service.AdmissionService;
import com.collegemate.model.Admission;

public class AdmissionPanel extends JPanel {

    private final Color primary = new Color(48, 91, 153);
    private final Color dark = new Color(22, 34, 58);
    private final Color background = new Color(245, 247, 251);

    private final AdmissionService service = new AdmissionService();

    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField courseField;
    private JTextField yearField;
    private JTextField dateField;
    private JTextField searchField;

    private JComboBox<String> statusBox;

    private JTable admissionTable;
    private DefaultTableModel tableModel;

    public AdmissionPanel() {

        setLayout(new BorderLayout(18, 18));
        setBackground(background);
        setBorder(new EmptyBorder(25, 28, 25, 28));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadAdmissions();
    }

    private JPanel createHeader() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admissions Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(dark);

        JLabel subtitle = new JLabel(
                "Manage student applications and admission status."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 120, 135));

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);

        return panel;
    }

    private JPanel createContent() {

        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);

        panel.add(createFormPanel(), BorderLayout.WEST);
        panel.add(createTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Application Details");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(dark);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));

        idField = addField(panel, "Application ID");
        nameField = addField(panel, "Student Name");
        emailField = addField(panel, "Email");
        phoneField = addField(panel, "Phone Number");
        courseField = addField(panel, "Course Name");
        yearField = addField(panel, "Academic Year");
        dateField = addField(panel, "Application Date (YYYY-MM-DD)");

        JLabel statusLabel = new JLabel("Application Status");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(dark);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(5));

        statusBox = new JComboBox<>(
                new String[]{"Pending", "Approved", "Rejected"}
        );

        statusBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 32)
        );
        statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(statusBox);
        panel.add(Box.createVerticalStrut(16));

        JButton addButton = createButton("Add Application");
        JButton updateButton = createButton("Update Selected");
        JButton deleteButton = createButton("Delete Selected");
        JButton clearButton = createButton("Clear Form");

        addButton.addActionListener(e -> addAdmission());
        updateButton.addActionListener(e -> updateAdmission());
        deleteButton.addActionListener(e -> deleteAdmission());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(updateButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(deleteButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearButton);

        return panel;
    }

    private JTextField addField(JPanel panel, String labelText) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(dark);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(9));

        return field;
    }

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JLabel heading = new JLabel("Admission Applications");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(dark);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(180, 32));
        searchField.setToolTipText("Search by name, ID, email or course");

        JButton searchButton = createButton("Search");
        JButton refreshButton = createButton("Refresh");

        searchButton.addActionListener(e -> searchAdmissions());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadAdmissions();
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Application ID",
                    "Student Name",
                    "Email",
                    "Phone",
                    "Course",
                    "Academic Year",
                    "Date",
                    "Status"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        admissionTable = new JTable(tableModel);
        admissionTable.setRowHeight(32);
        admissionTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        admissionTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        admissionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(admissionTable), BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setBackground(primary);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private Admission readForm() {

        return new Admission(
                idField.getText().trim(),
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                courseField.getText().trim(),
                yearField.getText().trim(),
                dateField.getText().trim(),
                statusBox.getSelectedItem().toString()
        );
    }

    private void addAdmission() {

        try {
            Admission admission = readForm();

            service.addAdmission(admission);

            JOptionPane.showMessageDialog(
                    this, "Application added successfully!"
            );

            clearForm();
            loadAdmissions();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateAdmission() {

        int row = admissionTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select an application first."
            );
            return;
        }

        try {
            String originalId = tableModel.getValueAt(row, 0).toString();

            Admission admission = readForm();

            boolean updated = service.updateAdmission(
                    originalId, admission
            );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this, "Application updated successfully!"
                );
                clearForm();
                loadAdmissions();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Application not found."
                );
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteAdmission() {

        int row = admissionTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select an application first."
            );
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this application?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = service.deleteAdmission(id);

            if (deleted) {
                JOptionPane.showMessageDialog(
                        this, "Application deleted successfully!"
                );
            }

            clearForm();
            loadAdmissions();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void searchAdmissions() {

        try {
            List<Admission> admissions =
                    service.searchAdmissions(searchField.getText());

            displayAdmissions(admissions);

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadAdmissions() {

        try {
            displayAdmissions(service.getAllAdmissions());
        } catch (Exception e) {
            showError(e);
        }
    }

    private void displayAdmissions(List<Admission> admissions) {

        tableModel.setRowCount(0);

        for (Admission admission : admissions) {

            tableModel.addRow(new Object[]{
                admission.getApplicationId(),
                admission.getStudentName(),
                admission.getEmail(),
                admission.getPhone(),
                admission.getCourseName(),
                admission.getAcademicYear(),
                admission.getApplicationDate(),
                admission.getStatus()
            });
        }
    }

    private void fillFormFromSelection() {

        int row = admissionTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        emailField.setText(tableModel.getValueAt(row, 2).toString());
        phoneField.setText(tableModel.getValueAt(row, 3).toString());
        courseField.setText(tableModel.getValueAt(row, 4).toString());
        yearField.setText(tableModel.getValueAt(row, 5).toString());
        dateField.setText(tableModel.getValueAt(row, 6).toString());
        statusBox.setSelectedItem(tableModel.getValueAt(row, 7).toString());
    }

    private void clearForm() {

        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        courseField.setText("");
        yearField.setText("");
        dateField.setText(LocalDate.now().toString());
        statusBox.setSelectedItem("Pending");

        admissionTable.clearSelection();
    }

    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage() == null
                        ? "Something went wrong."
                        : e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}