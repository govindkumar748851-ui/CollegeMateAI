package com.collegemate.frontend;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.collegemate.backend.service.StudentPlacementService;
import com.collegemate.model.StudentPlacement;

public class StudentPlacementPanel extends JPanel {

    private final StudentPlacementService service = new StudentPlacementService();
    private final Color primary = new Color(48, 91, 153);
    private final Color dark = new Color(22, 34, 58);

    private JTextField idField, studentIdField, nameField,
            companyField, roleField, dateField, searchField;

    private JComboBox<String> statusBox;
    private JTable table;
    private DefaultTableModel model;

    public StudentPlacementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(22, 25, 22, 25));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Placement Tracking");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(dark);

        JLabel subtitle = new JLabel("Track student applications and placement progress.");
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        return panel;
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.add(createForm(), BorderLayout.WEST);
        panel.add(createTable(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(290, 0));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("Application Details");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(12));

        idField = addField(panel, "Application ID");
        studentIdField = addField(panel, "Student ID");
        nameField = addField(panel, "Student Name");
        companyField = addField(panel, "Company Name");
        roleField = addField(panel, "Job Role");
        dateField = addField(panel, "Application Date (YYYY-MM-DD)");

        JLabel statusLabel = new JLabel("Placement Status");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(5));

        statusBox = new JComboBox<>(new String[]{
            "Applied", "Shortlisted", "Selected", "Rejected"
        });
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusBox);
        panel.add(Box.createVerticalStrut(14));

        JButton add = createButton("Add Application");
        JButton update = createButton("Update Selected");
        JButton delete = createButton("Delete Selected");
        JButton clear = createButton("Clear Form");

        add.addActionListener(e -> addApplication());
        update.addActionListener(e -> updateApplication());
        delete.addActionListener(e -> deleteApplication());
        clear.addActionListener(e -> clearForm());

        for (JButton button : new JButton[]{add, update, delete, clear}) {
            panel.add(button);
            panel.add(Box.createVerticalStrut(7));
        }

        return panel;
    }

    private JTextField addField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 31));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(8));
        return field;
    }

    private JPanel createTable() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);

        JLabel heading = new JLabel("Student Applications");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        JButton search = createButton("Search");
        JButton refresh = createButton("Refresh");

        search.addActionListener(e -> searchData());
        refresh.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(search, BorderLayout.EAST);

        top.add(heading, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);
        top.add(refresh, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{
            "Application ID", "Student ID", "Student Name",
            "Company", "Job Role", "Date", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillSelected();
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
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

    private StudentPlacement readForm() {
        return new StudentPlacement(
                idField.getText().trim(),
                studentIdField.getText().trim(),
                nameField.getText().trim(),
                companyField.getText().trim(),
                roleField.getText().trim(),
                dateField.getText().trim(),
                statusBox.getSelectedItem().toString()
        );
    }

    private void addApplication() {
        try {
            service.add(readForm());
            JOptionPane.showMessageDialog(this, "Application added successfully!");
            clearForm();
            loadData();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateApplication() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first.");
            return;
        }

        try {
            String id = model.getValueAt(row, 0).toString();
            if (service.update(id, readForm())) {
                JOptionPane.showMessageDialog(this, "Application updated!");
                clearForm();
                loadData();
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteApplication() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first.");
            return;
        }

        String id = model.getValueAt(row, 0).toString();

        if (JOptionPane.showConfirmDialog(
                this, "Delete this application?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.delete(id);
            clearForm();
            loadData();
            JOptionPane.showMessageDialog(this, "Application deleted.");
        } catch (Exception e) {
            showError(e);
        }
    }

    private void searchData() {
        try {
            display(service.search(searchField.getText()));
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadData() {
        try {
            display(service.getAll());
        } catch (Exception e) {
            showError(e);
        }
    }

    private void display(List<StudentPlacement> applications) {
        model.setRowCount(0);

        for (StudentPlacement p : applications) {
            model.addRow(new Object[]{
                p.getApplicationId(), p.getStudentId(),
                p.getStudentName(), p.getCompanyName(),
                p.getJobRole(), p.getApplicationDate(), p.getStatus()
            });
        }
    }

    private void fillSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        idField.setText(model.getValueAt(row, 0).toString());
        studentIdField.setText(model.getValueAt(row, 1).toString());
        nameField.setText(model.getValueAt(row, 2).toString());
        companyField.setText(model.getValueAt(row, 3).toString());
        roleField.setText(model.getValueAt(row, 4).toString());
        dateField.setText(model.getValueAt(row, 5).toString());
        statusBox.setSelectedItem(model.getValueAt(row, 6).toString());
    }

    private void clearForm() {
        idField.setText("");
        studentIdField.setText("");
        nameField.setText("");
        companyField.setText("");
        roleField.setText("");
        dateField.setText("");
        statusBox.setSelectedItem("Applied");
        table.clearSelection();
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