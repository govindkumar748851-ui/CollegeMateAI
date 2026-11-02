package com.collegemate.frontend;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.collegemate.backend.service.CourseService;
import com.collegemate.model.Course;

public class CoursePanel extends JPanel {

    private final Color primary = new Color(48, 91, 153);
    private final Color dark = new Color(22, 34, 58);
    private final Color muted = new Color(110, 120, 135);

    private final CourseService courseService = new CourseService();

    private JTextField idField;
    private JTextField nameField;
    private JTextField departmentField;
    private JTextField durationField;
    private JTextField eligibilityField;
    private JTextField feesField;
    private JTextArea descriptionArea;

    private DefaultTableModel tableModel;
    private JTable courseTable;

    public CoursePanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(25, 28, 25, 28));

        add(createHeading(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadCourses();
    }

    private JPanel createHeading() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Course Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(dark);

        JLabel subtitle = new JLabel(
                "Add, view, update and manage college courses."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(muted);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        text.add(title);
        text.add(Box.createVerticalStrut(6));
        text.add(subtitle);

        panel.add(text, BorderLayout.WEST);

        return panel;
    }

    private JPanel createMainContent() {

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

        JLabel heading = new JLabel("Course Details");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(dark);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));

        idField = addField(panel, "Course ID");
        nameField = addField(panel, "Course Name");
        departmentField = addField(panel, "Department");
        durationField = addField(panel, "Duration");
        eligibilityField = addField(panel, "Eligibility");
        feesField = addField(panel, "Annual Fees");

        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        descriptionLabel.setForeground(dark);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(descriptionLabel);
        panel.add(Box.createVerticalStrut(5));

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionScroll.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 75)
        );

        panel.add(descriptionScroll);
        panel.add(Box.createVerticalStrut(15));

        JButton addButton = createButton("Add Course");
        JButton updateButton = createButton("Update Selected");
        JButton deleteButton = createButton("Delete Selected");
        JButton clearButton = createButton("Clear Form");

        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
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
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));

        return field;
    }

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel heading = new JLabel("Available Courses");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(dark);

        tableModel = new DefaultTableModel(
                new String[]{
                    "Course ID", "Course Name", "Department",
                    "Duration", "Fees"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(34);
        courseTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        courseTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseTable);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setBackground(primary);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        return button;
    }

    private Course readForm() {

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String duration = durationField.getText().trim();
        String eligibility = eligibilityField.getText().trim();
        String feesText = feesField.getText().trim();
        String description = descriptionArea.getText().trim();

        double fees;

        try {
            fees = Double.parseDouble(feesText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Please enter a valid fees amount."
            );
        }

        return new Course(
                id, name, department, duration,
                eligibility, fees, description
        );
    }

    private void addCourse() {

        try {
            Course course = readForm();
            courseService.addCourse(course);

            JOptionPane.showMessageDialog(
                    this, "Course added successfully!"
            );

            clearForm();
            loadCourses();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateCourse() {

        int row = courseTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a course first."
            );
            return;
        }

        try {
            String originalId = tableModel.getValueAt(row, 0).toString();
            Course course = readForm();

            boolean updated = courseService.updateCourse(
                    originalId, course
            );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this, "Course updated successfully!"
                );
                clearForm();
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Course could not be found."
                );
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteCourse() {

        int row = courseTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a course first."
            );
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this course?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = courseService.deleteCourse(id);

            if (deleted) {
                JOptionPane.showMessageDialog(
                        this, "Course deleted successfully!"
                );
            }

            clearForm();
            loadCourses();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadCourses() {

        try {
            List<Course> courses = courseService.getAllCourses();

            tableModel.setRowCount(0);

            for (Course course : courses) {
                tableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getDepartment(),
                    course.getDuration(),
                    course.getFees()
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void fillFormFromSelection() {

        int row = courseTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();

        try {
            Course course = courseService.getCourseById(id);

            if (course == null) {
                return;
            }

            idField.setText(course.getCourseId());
            nameField.setText(course.getCourseName());
            departmentField.setText(course.getDepartment());
            durationField.setText(course.getDuration());
            eligibilityField.setText(course.getEligibility());
            feesField.setText(String.valueOf(course.getFees()));
            descriptionArea.setText(course.getDescription());

        } catch (Exception e) {
            showError(e);
        }
    }

    private void clearForm() {

        idField.setText("");
        nameField.setText("");
        departmentField.setText("");
        durationField.setText("");
        eligibilityField.setText("");
        feesField.setText("");
        descriptionArea.setText("");

        courseTable.clearSelection();
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