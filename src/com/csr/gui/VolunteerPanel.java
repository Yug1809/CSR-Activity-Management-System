
package com.csr.gui;

import com.csr.dao.VolunteerDAO;
import com.csr.service.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

public class VolunteerPanel extends JPanel {

    // ================= COLORS =================
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color SECONDARY = new Color(100, 116, 139);
    private final Color BORDER = new Color(226, 232, 240);
    private final Color SUCCESS = new Color(22, 163, 74);

    // ================= COMPONENTS =================
    private JTable volunteerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private JComboBox<VolunteerDAO.EventOption> eventFilterBox;

    private final VolunteerDAO volunteerDAO = new VolunteerDAO();

    // ================= CONSTRUCTOR =================
    public VolunteerPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ================= TOP SECTION =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Volunteers");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Manage volunteers and their participation in CSR activities");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton addVolunteerButton = createButton(
                "+  Add New Volunteer", PRIMARY, Color.WHITE);

        addVolunteerButton.addActionListener(
                e -> showAddVolunteerDialog());

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(addVolunteerButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // ================= SEARCH PANEL =================
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setBackground(CARD);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel searchLabel = new JLabel("Search Volunteers");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT);

        // Event filter dropdown
        eventFilterBox = new JComboBox<>();
        eventFilterBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        eventFilterBox.setPreferredSize(new Dimension(190, 38));

        // Search field
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 12, 0, 12)));

        JButton searchButton = createButton(
                "Search", PRIMARY, Color.WHITE);

        JButton resetButton = createButton(
                "Reset", new Color(241, 245, 249), TEXT);

        // Search controls
        JPanel searchInputPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));

        searchInputPanel.setOpaque(false);
        searchInputPanel.add(eventFilterBox);
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        searchInputPanel.add(resetButton);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE CARD =================
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createLineBorder(BORDER));

        String[] columns = {
                "ID", "Name", "Email", "Phone", "Events",
                "Attendance", "Status", "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        volunteerTable = new JTable(tableModel);
        volunteerTable.setRowHeight(48);
        volunteerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        volunteerTable.setForeground(TEXT);
        volunteerTable.setGridColor(BORDER);
        volunteerTable.setSelectionBackground(new Color(239, 246, 255));
        volunteerTable.setSelectionForeground(TEXT);
        volunteerTable.setShowVerticalLines(false);

        volunteerTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        volunteerTable.getTableHeader().setForeground(TEXT);
        volunteerTable.getTableHeader().setBackground(
                new Color(248, 250, 252));

        volunteerTable.getTableHeader().setPreferredSize(
                new Dimension(0, 45));

        int[] widths = {60, 170, 210, 120, 80, 100, 90, 120};

        for (int i = 0; i < widths.length; i++) {
            volunteerTable.getColumnModel()
                    .getColumn(i).setPreferredWidth(widths[i]);
        }

        centerColumn(0);
        centerColumn(3);
        centerColumn(4);
        centerColumn(5);
        centerColumn(6);
        centerColumn(7);

        volunteerTable.getColumnModel().getColumn(6)
                .setCellRenderer(new StatusRenderer());

        volunteerTable.getColumnModel().getColumn(7)
                .setCellRenderer(new ActionRenderer());

        // ================= TABLE DOUBLE CLICK =================
        volunteerTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    return;
                }

                int viewRow = volunteerTable.rowAtPoint(e.getPoint());

                if (viewRow < 0) {
                    return;
                }

                int viewColumn =
                        volunteerTable.columnAtPoint(e.getPoint());

                int modelRow =
                        volunteerTable.convertRowIndexToModel(viewRow);

                if (viewColumn == 7) {
                    showVolunteerActions(modelRow);
                } else {
                    showVolunteerDetails(modelRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(volunteerTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // ================= LOAD EVENT FILTER =================
        loadEventFilter();

        // ================= DATABASE LOAD =================
        loadVolunteersFromDatabase();

        // ================= SEARCH ACTIONS =================
        searchButton.addActionListener(e -> searchVolunteers());

        searchField.addActionListener(e -> searchVolunteers());

        // Reset search and event filter
        resetButton.addActionListener(e -> {
            searchField.setText("");
            eventFilterBox.setSelectedIndex(0);
            loadVolunteersFromDatabase();
        });

        // Automatically reload when event changes
        eventFilterBox.addActionListener(e ->
                loadVolunteerRows(searchField.getText().trim()));
    }

    // =====================================================
    // CREATE BUTTON
    // =====================================================
    private JButton createButton(
            String text, Color background, Color foreground) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));

        return button;
    }

    // =====================================================
    // LOAD EVENT FILTER FROM MYSQL
    // =====================================================
    private void loadEventFilter() {

        eventFilterBox.removeAllItems();

        // All Events option
        eventFilterBox.addItem(
                new VolunteerDAO.EventOption(0, "All Events"));

        try {

            List<VolunteerDAO.EventOption> events =
                    volunteerDAO.getAllEvents();

            for (VolunteerDAO.EventOption event : events) {
                eventFilterBox.addItem(event);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load event filters:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
        }

        eventFilterBox.setSelectedIndex(0);
    }

    // =====================================================
    // LOAD VOLUNTEERS FROM MYSQL
    // =====================================================
    private void loadVolunteersFromDatabase() {
        loadVolunteerRows(null);
    }

    private void loadVolunteerRows(String search) {

        try {

            VolunteerDAO.EventOption selectedEvent =
                    (VolunteerDAO.EventOption)
                            eventFilterBox.getSelectedItem();

            List<VolunteerDAO.VolunteerRecord> volunteers;

            if (selectedEvent != null && selectedEvent.id != 0) {

                // Load volunteers for selected event
                volunteers = volunteerDAO.getVolunteersByEvent(
                        selectedEvent.id, search);

            } else {

                // Load all volunteers
                volunteers = (search == null || search.trim().isEmpty())
                        ? volunteerDAO.getAllVolunteers()
                        : volunteerDAO.searchVolunteers(search);
            }

            tableModel.setRowCount(0);

            for (VolunteerDAO.VolunteerRecord volunteer : volunteers) {

                tableModel.addRow(new Object[]{

                        "V" + String.format(
                                "%03d", volunteer.volunteerId),

                        volunteer.name,
                        volunteer.email,
                        volunteer.phone,
                        volunteer.eventCount,
                        volunteer.attendance,
                        volunteer.status,
                        "View / Edit"
                });
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load volunteers from MySQL:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
        }
    }

    // =====================================================
    // ADD VOLUNTEER DIALOG
    // =====================================================
    private void showAddVolunteerDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add New Volunteer",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setSize(500, 680);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Add New Volunteer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(createLabel("Full Name"));

        JTextField nameField = createTextField();
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(12));

        mainPanel.add(createLabel(
                "Email Address (also used as username)"));

        JTextField emailField = createTextField();
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(12));

        mainPanel.add(createLabel("Phone Number"));

        JTextField phoneField = createTextField();
        mainPanel.add(phoneField);
        mainPanel.add(Box.createVerticalStrut(12));

        mainPanel.add(createLabel("Temporary Login Password"));

        JPasswordField passwordField = createPasswordField();
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(12));

        mainPanel.add(createLabel("Select CSR Event"));

        JComboBox<VolunteerDAO.EventOption> eventBox =
                new JComboBox<>();

        eventBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eventBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40));

        mainPanel.add(eventBox);
        mainPanel.add(Box.createVerticalStrut(22));

        // Load events into registration dropdown
        try {

            List<VolunteerDAO.EventOption> events =
                    volunteerDAO.getAllEvents();

            for (VolunteerDAO.EventOption event : events) {
                eventBox.addItem(event);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    dialog,
                    "Unable to load events from MySQL:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
            dialog.dispose();
            return;
        }

        if (eventBox.getItemCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No events are available. Create an event first.",
                    "No Events Found",
                    JOptionPane.WARNING_MESSAGE);

            dialog.dispose();
            return;
        }

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setOpaque(false);

        JButton cancelButton = createButton(
                "Cancel", new Color(241, 245, 249), TEXT);

        JButton saveButton = createButton(
                "Save Volunteer", PRIMARY, Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel);

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {

            String name = nameField.getText().trim();

            String email = emailField.getText()
                    .trim().toLowerCase();

            String phone = phoneField.getText().trim();

            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            VolunteerDAO.EventOption selectedEvent =
                    (VolunteerDAO.EventOption)
                            eventBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty()
                    || phone.isEmpty() || password.isEmpty()
                    || selectedEvent == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please fill all fields and select an event.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);

                Arrays.fill(passwordChars, '\0');
                return;
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter a valid email address.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);

                Arrays.fill(passwordChars, '\0');
                return;
            }

            if (!phone.matches("[0-9]{10,15}")) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter a valid phone number (10 to 15 digits).",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);

                Arrays.fill(passwordChars, '\0');
                return;
            }

            if (password.length() < 8) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Password must contain at least 8 characters.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);

                Arrays.fill(passwordChars, '\0');
                return;
            }

            try {

                String passwordHash =
                        PasswordUtil.hashPassword(password);

                volunteerDAO.addVolunteer(
                        name,
                        email,
                        phone,
                        passwordHash,
                        selectedEvent.id);

                // Refresh current event filter
                loadVolunteerRows(searchField.getText().trim());

                JOptionPane.showMessageDialog(
                        dialog,
                        "Volunteer registered successfully!\n"
                                + "Login username: " + email,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Unable to register volunteer:\n"
                                + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                ex.printStackTrace();

            } finally {

                Arrays.fill(passwordChars, '\0');
                passwordField.setText("");
            }
        });

        JScrollPane formScroll = new JScrollPane(mainPanel);
        formScroll.setBorder(null);

        dialog.setContentPane(formScroll);
        dialog.setVisible(true);
    }

    // =====================================================
    // FORM COMPONENTS
    // =====================================================
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JTextField createTextField() {

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 10, 0, 10)));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        return field;
    }

    private JPasswordField createPasswordField() {

        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 10, 0, 10)));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        return field;
    }

    // =====================================================
    // SEARCH VOLUNTEERS
    // =====================================================
    private void searchVolunteers() {

        loadVolunteerRows(searchField.getText().trim());

        if (tableModel.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No volunteers found.",
                    "Search Result",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =====================================================
    // CENTER COLUMN
    // =====================================================
    private void centerColumn(int column) {

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(
                SwingConstants.CENTER);

        volunteerTable.getColumnModel()
                .getColumn(column)
                .setCellRenderer(renderer);
    }

    // =====================================================
    // VOLUNTEER ACTIONS
    // =====================================================
    private void showVolunteerActions(int row) {

        String idText =
                tableModel.getValueAt(row, 0).toString();

        // Displayed ID represents USERS.id
        int userId = Integer.parseInt(idText.substring(1));

        String status =
                tableModel.getValueAt(row, 6).toString();

        if ("Inactive".equalsIgnoreCase(status)) {

            JOptionPane.showMessageDialog(
                    this,
                    "This volunteer is inactive.",
                    "Volunteer Inactive",
                    JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        Object[] options = {
                "Edit Details",
                "Deactivate",
                "Cancel"
        };

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose an action for this volunteer.",
                "Volunteer Actions",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {

            showEditVolunteerDialog(row, userId);

        } else if (choice == 1) {

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Deactivate this volunteer? Their event and attendance history will be preserved.",
                    "Confirm Deactivation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {

                try {

                    volunteerDAO.deactivateVolunteer(userId);

                    loadVolunteerRows(searchField.getText().trim());

                    JOptionPane.showMessageDialog(
                            this,
                            "Volunteer deactivated successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to deactivate volunteer:\n"
                                    + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);

                    ex.printStackTrace();
                }
            }
        }
    }

    // =====================================================
    // EDIT VOLUNTEER
    // =====================================================
    private void showEditVolunteerDialog(int row, int userId) {

        JTextField nameField = new JTextField(
                tableModel.getValueAt(row, 1).toString());

        JTextField emailField = new JTextField(
                tableModel.getValueAt(row, 2).toString());

        JTextField phoneField = new JTextField(
                tableModel.getValueAt(row, 3).toString());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));

        form.add(new JLabel("Full Name"));
        form.add(nameField);

        form.add(new JLabel("Email Address / Username"));
        form.add(emailField);

        form.add(new JLabel("Phone Number"));
        form.add(phoneField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Volunteer Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();

        String email = emailField.getText()
                .trim().toLowerCase();

        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty()
                || phone.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid email address.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid phone number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        try {

            volunteerDAO.updateVolunteer(
                    userId, name, email, phone);

            loadVolunteerRows(searchField.getText().trim());

            JOptionPane.showMessageDialog(
                    this,
                    "Volunteer details updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update volunteer:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
        }
    }

    // =====================================================
    // VOLUNTEER DETAILS
    // =====================================================
    private void showVolunteerDetails(int row) {

        String id = tableModel.getValueAt(row, 0).toString();
        String name = tableModel.getValueAt(row, 1).toString();
        String email = tableModel.getValueAt(row, 2).toString();
        String phone = tableModel.getValueAt(row, 3).toString();
        String events = tableModel.getValueAt(row, 4).toString();
        String attendance = tableModel.getValueAt(row, 5).toString();
        String status = tableModel.getValueAt(row, 6).toString();

        String message =
                "Volunteer ID: " + id
                        + "\nName: " + name
                        + "\nEmail: " + email
                        + "\nPhone: " + phone
                        + "\nEvents Participated: " + events
                        + "\nAttendance: " + attendance
                        + "\nStatus: " + status;

        JOptionPane.showMessageDialog(
                this,
                message,
                "Volunteer Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // =====================================================
    // STATUS RENDERER
    // =====================================================
    private class StatusRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel label = new JLabel(
                    value == null ? "" : value.toString());

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            if ("Active".equals(value)) {

                label.setForeground(SUCCESS);
                label.setBackground(new Color(220, 252, 231));

            } else {

                label.setForeground(new Color(180, 83, 9));
                label.setBackground(new Color(254, 243, 199));
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }

            return label;
        }
    }

    // =====================================================
    // ACTION RENDERER
    // =====================================================
    private class ActionRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel label = new JLabel(
                    value == null ? "" : value.toString());

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(PRIMARY);

            return label;
        }
    }
}
