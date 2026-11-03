package com.csr.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EventPanel extends JPanel {

    // ================= COLORS =================
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color PRIMARY_DARK = new Color(29, 78, 216);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color SECONDARY = new Color(100, 116, 139);
    private final Color BORDER = new Color(226, 232, 240);
    private final Color SUCCESS = new Color(22, 163, 74);
    private final Color WARNING = new Color(245, 158, 11);
    private final Color DANGER = new Color(220, 38, 38);

    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public EventPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ================= TOP SECTION =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Events");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Create, manage and monitor CSR activities"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        // ADD EVENT BUTTON
        JButton addEventButton = createButton(
                "+  Add New Event",
                PRIMARY,
                Color.WHITE
        );

        addEventButton.addActionListener(e -> showAddEventDialog());

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(addEventButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // ================= SEARCH BAR =================
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setBackground(CARD);
        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel searchLabel = new JLabel("Search Events");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 38));
        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(0, 12, 0, 12)
                )
        );

        JButton searchButton = createButton(
                "Search",
                PRIMARY,
                Color.WHITE
        );

        JButton resetButton = createButton(
                "Reset",
                new Color(241, 245, 249),
                TEXT
        );

        JPanel searchInputPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 10, 0
        ));
        searchInputPanel.setOpaque(false);

        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        searchInputPanel.add(resetButton);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        String[] columns = {
                "ID",
                "Event Name",
                "Date",
                "Location",
                "Volunteers",
                "Budget",
                "Status",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventTable = new JTable(tableModel);

        eventTable.setRowHeight(48);
        eventTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        eventTable.setForeground(TEXT);
        eventTable.setGridColor(BORDER);
        eventTable.setSelectionBackground(new Color(239, 246, 255));
        eventTable.setSelectionForeground(TEXT);

        eventTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        eventTable.getTableHeader().setForeground(TEXT);
        eventTable.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );

        eventTable.getTableHeader().setPreferredSize(
                new Dimension(0, 45)
        );

        eventTable.setShowVerticalLines(false);

        // Column widths
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        eventTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        eventTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        // Center some columns
        centerColumn(0);
        centerColumn(2);
        centerColumn(4);
        centerColumn(5);
        centerColumn(6);
        centerColumn(7);

        // Status renderer
        eventTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new StatusRenderer());

        // Actions renderer
        eventTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(new ActionRenderer());

        // Double click
        eventTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int row = eventTable.getSelectedRow();

                    if (row != -1) {
                        showEventDetails(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // ================= LOAD EVENTS FROM DATABASE =================
        loadEventsFromDatabase();

        // ================= SEARCH =================
        searchButton.addActionListener(e -> searchEvents());

        resetButton.addActionListener(e -> {

            searchField.setText("");
            loadEventsFromDatabase();
        });
    }
    private void loadEventsFromDatabase() {
        try {
            com.csr.dao.EventDAO dao = new com.csr.dao.EventDAO();
            java.util.List<com.csr.dao.EventDAO.EventRecord> events = dao.getAllEvents();

            tableModel.setRowCount(0);
            for (com.csr.dao.EventDAO.EventRecord event : events) {
                tableModel.addRow(new Object[]{
                        String.format("EV%03d", event.id),
                        event.name,
                        event.date,
                        event.location,
                        event.volunteerCount,
                        event.budget,
                        event.status,
                        "View / Edit"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load events from the database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    // =====================================================
    // CREATE BUTTON
    // =====================================================

    private JButton createButton(
            String text,
            Color background,
            Color foreground
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        button.setBackground(background);
        button.setForeground(foreground);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                new EmptyBorder(10, 18, 10, 18)
        );

        return button;
    }

    // =====================================================
    // LOAD SAMPLE EVENTS
    // =====================================================



    // =====================================================
    // ADD EVENT DIALOG
    // =====================================================

    private void showAddEventDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add New Event",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        mainPanel.setLayout(
                new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Create New Event");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );

        title.setForeground(TEXT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(25));

        // Event Name
        mainPanel.add(createLabel("Event Name"));

        JTextField eventNameField = createTextField();

        mainPanel.add(eventNameField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Date
        mainPanel.add(createLabel("Event Date"));

        JTextField dateField = createTextField();

        mainPanel.add(dateField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Location
        mainPanel.add(createLabel("Location"));

        JTextField locationField = createTextField();

        mainPanel.add(locationField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Volunteer Count
        mainPanel.add(createLabel("Expected Volunteers"));

        JTextField volunteerField = createTextField();

        mainPanel.add(volunteerField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Budget
        mainPanel.add(createLabel("Budget"));

        JTextField budgetField = createTextField();

        mainPanel.add(budgetField);
        mainPanel.add(Box.createVerticalStrut(25));

        // Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setOpaque(false);

        JButton cancelButton = createButton(
                "Cancel",
                new Color(241, 245, 249),
                TEXT
        );

        JButton saveButton = createButton(
                "Save Event",
                PRIMARY,
                Color.WHITE
        );

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel);

        cancelButton.addActionListener(e ->
                dialog.dispose()
        );

        saveButton.addActionListener(e -> {
            String name = eventNameField.getText().trim();
            String dateText = dateField.getText().trim();
            String location = locationField.getText().trim();
            String volunteersText = volunteerField.getText().trim();
            String budgetText = budgetField.getText().trim();

            if (name.isEmpty() || dateText.isEmpty() || location.isEmpty()
                    || volunteersText.isEmpty() || budgetText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog, "Please fill all fields.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                java.sql.Date sqlDate = java.sql.Date.valueOf(dateText);
                int expectedVolunteers = Integer.parseInt(volunteersText);
                java.math.BigDecimal eventBudget = new java.math.BigDecimal(budgetText);

                if (expectedVolunteers < 0 || eventBudget.signum() < 0) {
                    JOptionPane.showMessageDialog(
                            dialog, "Volunteers and budget cannot be negative.",
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                com.csr.dao.EventDAO dao = new com.csr.dao.EventDAO();
                dao.addEvent(name, "", "General", location, sqlDate, eventBudget);

                loadEventsFromDatabase();
                JOptionPane.showMessageDialog(
                        dialog, "Event saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter the date as yyyy-MM-dd, a whole number of volunteers, and a valid budget.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog, "Failed to save event: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // =====================================================
    // LABEL
    // =====================================================

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        label.setForeground(TEXT);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    // =====================================================
    // TEXT FIELD
    // =====================================================

    private JTextField createTextField() {

        JTextField field = new JTextField();

        field.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(0, 10, 0, 10)
                )
        );

        return field;
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void searchEvents() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadEventsFromDatabase();
            return;
        }

        try {
            com.csr.dao.EventDAO dao = new com.csr.dao.EventDAO();
            java.util.List<com.csr.dao.EventDAO.EventRecord> events =
                    dao.searchEvents(searchText);

            tableModel.setRowCount(0);
            for (com.csr.dao.EventDAO.EventRecord event : events) {
                tableModel.addRow(new Object[]{
                        String.format("EV%03d", event.id),
                        event.name,
                        event.date,
                        event.location,
                        event.volunteerCount,
                        event.budget,
                        event.status,
                        "View / Edit"
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this, "No events found.",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, "Unable to search events: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // =====================================================
    // CENTER COLUMN
    // =====================================================

    private void centerColumn(int column) {

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        eventTable.getColumnModel()
                .getColumn(column)
                .setCellRenderer(renderer);
    }

    // =====================================================
    // EVENT DETAILS
    // =====================================================

    private void showEventDetails(int row) {

        String id =
                tableModel.getValueAt(row, 0).toString();

        String name =
                tableModel.getValueAt(row, 1).toString();

        String date =
                tableModel.getValueAt(row, 2).toString();

        String location =
                tableModel.getValueAt(row, 3).toString();

        String volunteers =
                tableModel.getValueAt(row, 4).toString();

        String budget =
                tableModel.getValueAt(row, 5).toString();

        String status =
                tableModel.getValueAt(row, 6).toString();

        String message =
                "Event ID: " + id +
                        "\nEvent Name: " + name +
                        "\nDate: " + date +
                        "\nLocation: " + location +
                        "\nVolunteers: " + volunteers +
                        "\nBudget: " + budget +
                        "\nStatus: " + status;

        JOptionPane.showMessageDialog(
                this,
                message,
                "Event Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // STATUS RENDERER
    // =====================================================

    private class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label = new JLabel(
                    value.toString()
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setOpaque(true);

            label.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    )
            );

            if (value.toString().equals("Completed")) {

                label.setForeground(SUCCESS);
                label.setBackground(
                        new Color(220, 252, 231)
                );

            } else if (
                    value.toString().equals("Upcoming")
            ) {

                label.setForeground(
                        new Color(180, 83, 9)
                );

                label.setBackground(
                        new Color(254, 243, 199)
                );
            }

            return label;
        }
    }

    // =====================================================
    // ACTION RENDERER
    // =====================================================

    private class ActionRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label = new JLabel(
                    value.toString()
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    )
            );

            label.setForeground(PRIMARY);

            return label;
        }
    }
}