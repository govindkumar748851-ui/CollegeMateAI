package com.collegemate.frontend;

import com.collegemate.backend.service.AuthService;
import com.collegemate.backend.service.AuthSession;
import com.collegemate.backend.service.UserSessionService;
import com.collegemate.model.AppUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final AuthService service = new AuthService();

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{
            "User ID", "Name", "Email", "Role",
            "Approval", "Account Status", "Online Status"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final Timer refreshTimer;
    private boolean loading = false;

    public UserManagementPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(new Color(22, 34, 58));

        JButton addButton = new JButton("Add Staff User");
        JButton approveButton = new JButton("Approve Staff");
        JButton rejectButton = new JButton("Reject Staff");
        JButton toggleButton = new JButton("Activate / Deactivate");
        JButton refreshButton = new JButton("Refresh");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(addButton);
        actions.add(approveButton);
        actions.add(rejectButton);
        actions.add(toggleButton);
        actions.add(refreshButton);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setAutoCreateRowSorter(true);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> addStaff());
        approveButton.addActionListener(e -> approveStaff());
        rejectButton.addActionListener(e -> rejectStaff());
        toggleButton.addActionListener(e -> toggleUser());
        refreshButton.addActionListener(e -> loadUsers());

        boolean admin = AuthSession.isAdmin();

        addButton.setEnabled(admin);
        approveButton.setEnabled(admin);
        rejectButton.setEnabled(admin);
        toggleButton.setEnabled(admin);

        refreshTimer = new Timer(15000, e -> loadUsers());
        refreshTimer.start();

        loadUsers();
    }

    private String selectedUserId() {

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return null;
        }

        row = table.convertRowIndexToModel(row);
        return model.getValueAt(row, 0).toString();
    }

    private String selectedRole() {

        int row = table.getSelectedRow();
        if (row < 0) return "";

        row = table.convertRowIndexToModel(row);
        return model.getValueAt(row, 3).toString();
    }

    private String selectedApproval() {

        int row = table.getSelectedRow();
        if (row < 0) return "";

        row = table.convertRowIndexToModel(row);
        return model.getValueAt(row, 4).toString();
    }

    private void addStaff() {

        if (!AuthSession.isAdmin()) return;

        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();

        Object[] fields = {
            "Full name:", name,
            "Email:", email,
            "Password (minimum 8 characters):", password
        };

        int choice = JOptionPane.showConfirmDialog(
            this, fields, "Add Staff Account", JOptionPane.OK_CANCEL_OPTION
        );

        if (choice != JOptionPane.OK_OPTION) return;

        try {
            service.createUser(
                name.getText(),
                email.getText(),
                new String(password.getPassword()),
                "STAFF"
            );

            JOptionPane.showMessageDialog(
                this, "Staff account created and approved."
            );

            loadUsers();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void approveStaff() {

        if (!AuthSession.isAdmin()) return;

        String id = selectedUserId();
        if (id == null) return;

        if (!"STAFF".equals(selectedRole())
                || !"PENDING".equals(selectedApproval())) {

            JOptionPane.showMessageDialog(
                this, "Select a pending Staff account."
            );
            return;
        }

        service.approveStaff(id);

        JOptionPane.showMessageDialog(this, "Staff account approved.");
        loadUsers();
    }

    private void rejectStaff() {

        if (!AuthSession.isAdmin()) return;

        String id = selectedUserId();
        if (id == null) return;

        if (!"STAFF".equals(selectedRole())
                || !"PENDING".equals(selectedApproval())) {

            JOptionPane.showMessageDialog(
                this, "Select a pending Staff account."
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reject this Staff registration?",
            "Reject Registration",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        service.rejectStaff(id);

        JOptionPane.showMessageDialog(
            this, "Staff registration rejected."
        );

        loadUsers();
    }

    private void toggleUser() {

        if (!AuthSession.isAdmin()) return;

        String id = selectedUserId();
        if (id == null) return;

        String role = selectedRole();

        if ("ADMIN".equals(role)) {
            JOptionPane.showMessageDialog(
                this, "Admin accounts cannot be changed here."
            );
            return;
        }

        int row = table.getSelectedRow();
        row = table.convertRowIndexToModel(row);

        String accountStatus = model.getValueAt(row, 5).toString();

        service.setUserActive(id, !"Active".equals(accountStatus));

        loadUsers();
    }

    private void loadUsers() {

        if (loading) return;

        loading = true;

        new SwingWorker<List<AppUser>, Void>() {

            @Override
            protected List<AppUser> doInBackground() {
                return service.getAllUsers();
            }

            @Override
            protected void done() {

                try {
                    List<AppUser> users = get();

                    model.setRowCount(0);

                    for (AppUser user : users) {

                        boolean online =
                            UserSessionService.isUserOnline(user.getUserId());

                        model.addRow(new Object[]{
                            user.getUserId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getApprovalStatus(),
                            user.isActive() ? "Active" : "Inactive",
                            online ? "Online" : "Offline"
                        });
                    }

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                        UserManagementPanel.this,
                        "Could not load users: " + e.getMessage()
                    );

                } finally {
                    loading = false;
                }
            }

        }.execute();
    }
}