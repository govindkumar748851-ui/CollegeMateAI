package com.collegemate.frontend;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.collegemate.backend.service.PlacementService;
import com.collegemate.model.Placement;

public class PlacementPanel extends JPanel {

    private final Color primary = new Color(48, 91, 153);
    private final Color dark = new Color(22, 34, 58);

    private final PlacementService service = new PlacementService();

    private JTextField idField, companyField, roleField,
            eligibilityField, locationField, packageField,
            deadlineField, searchField;

    private JComboBox<String> statusBox;
    private JTable table;
    private DefaultTableModel model;

    public PlacementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(22, 25, 22, 25));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadPlacements();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Placements Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(dark);

        JLabel subtitle = new JLabel("Manage companies and job opportunities.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

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
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("Opportunity Details");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(dark);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(12));

        idField = addField(panel, "Placement ID");
        companyField = addField(panel, "Company Name");
        roleField = addField(panel, "Job Role");
        eligibilityField = addField(panel, "Eligibility");
        locationField = addField(panel, "Location");
        packageField = addField(panel, "Package (LPA)");
        deadlineField = addField(panel, "Application Deadline");

        JLabel statusLabel = new JLabel("Opportunity Status");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(5));

        statusBox = new JComboBox<>(new String[]{"Open", "Closed", "On Hold"});
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusBox);
        panel.add(Box.createVerticalStrut(14));

        JButton add = createButton("Add Opportunity");
        JButton update = createButton("Update Selected");
        JButton delete = createButton("Delete Selected");
        JButton clear = createButton("Clear Form");

        add.addActionListener(e -> addPlacement());
        update.addActionListener(e -> updatePlacement());
        delete.addActionListener(e -> deletePlacement());
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

        JLabel heading = new JLabel("Available Opportunities");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(dark);

        JPanel search = new JPanel(new BorderLayout(6, 0));
        search.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 32));

        JButton searchButton = createButton("Search");
        JButton refreshButton = createButton("Refresh");

        searchButton.addActionListener(e -> searchPlacements());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadPlacements();
        });

        search.add(searchField, BorderLayout.CENTER);
        search.add(searchButton, BorderLayout.EAST);

        top.add(heading, BorderLayout.NORTH);
        top.add(search, BorderLayout.CENTER);
        top.add(refreshButton, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{
            "ID", "Company", "Job Role", "Eligibility",
            "Location", "Package LPA", "Deadline", "Status"
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

    private Placement readForm() {
        double packageValue;

        try {
            packageValue = Double.parseDouble(packageField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid package amount.");
        }

        return new Placement(
                idField.getText().trim(),
                companyField.getText().trim(),
                roleField.getText().trim(),
                eligibilityField.getText().trim(),
                locationField.getText().trim(),
                packageValue,
                deadlineField.getText().trim(),
                statusBox.getSelectedItem().toString()
        );
    }

    private void addPlacement() {
        try {
            service.addPlacement(readForm());
            JOptionPane.showMessageDialog(this, "Opportunity added successfully!");
            clearForm();
            loadPlacements();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void updatePlacement() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an opportunity first.");
            return;
        }

        try {
            String originalId = model.getValueAt(row, 0).toString();
            boolean updated = service.updatePlacement(originalId, readForm());

            if (updated) {
                JOptionPane.showMessageDialog(this, "Opportunity updated!");
                clearForm();
                loadPlacements();
            } else {
                JOptionPane.showMessageDialog(this, "Opportunity not found.");
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deletePlacement() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an opportunity first.");
            return;
        }

        String id = model.getValueAt(row, 0).toString();

        int choice = JOptionPane.showConfirmDialog(
                this, "Delete this opportunity?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.deletePlacement(id);
            clearForm();
            loadPlacements();
            JOptionPane.showMessageDialog(this, "Opportunity deleted.");
        } catch (Exception e) {
            showError(e);
        }
    }

    private void searchPlacements() {
        try {
            display(service.searchPlacements(searchField.getText()));
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadPlacements() {
        try {
            display(service.getAllPlacements());
        } catch (Exception e) {
            showError(e);
        }
    }

    private void display(List<Placement> placements) {
        model.setRowCount(0);

        for (Placement p : placements) {
            model.addRow(new Object[]{
                p.getPlacementId(),
                p.getCompanyName(),
                p.getJobRole(),
                p.getEligibility(),
                p.getLocation(),
                p.getPackageLPA(),
                p.getApplicationDeadline(),
                p.getStatus()
            });
        }
    }

    private void fillSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String id = model.getValueAt(row, 0).toString();

        try {
            Placement p = service.getPlacementById(id);
            if (p == null) return;

            idField.setText(p.getPlacementId());
            companyField.setText(p.getCompanyName());
            roleField.setText(p.getJobRole());
            eligibilityField.setText(p.getEligibility());
            locationField.setText(p.getLocation());
            packageField.setText(String.valueOf(p.getPackageLPA()));
            deadlineField.setText(p.getApplicationDeadline());
            statusBox.setSelectedItem(p.getStatus());
        } catch (Exception e) {
            showError(e);
        }
    }

    private void clearForm() {
        idField.setText("");
        companyField.setText("");
        roleField.setText("");
        eligibilityField.setText("");
        locationField.setText("");
        packageField.setText("");
        deadlineField.setText("");
        statusBox.setSelectedItem("Open");
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