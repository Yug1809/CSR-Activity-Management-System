
package com.csr.gui;

import com.csr.dao.AttendanceDAO;
import com.csr.service.AttendanceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendancePanel extends JPanel {

    // ================= COLORS =================

    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color SECONDARY = new Color(100, 116, 139);
    private final Color BORDER = new Color(226, 232, 240);
    private final Color SUCCESS = new Color(22, 163, 74);
    private final Color DANGER = new Color(220, 38, 38);

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private final AttendanceService attendanceService;

    private final DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ================= CONSTRUCTOR =================

    public AttendancePanel() {

        attendanceService = new AttendanceService();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ================= TOP SECTION =================

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Attendance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Track volunteer participation in CSR activities"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton addAttendanceButton = createButton(
                "+  Mark Attendance",
                PRIMARY,
                Color.WHITE
        );

        addAttendanceButton.addActionListener(
                e -> showAttendanceDialog()
        );

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(addAttendanceButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= CONTENT =================

        JPanel contentPanel = new JPanel(
                new BorderLayout(0, 20)
        );

        contentPanel.setOpaque(false);
        contentPanel.setBorder(
                new EmptyBorder(25, 0, 0, 0)
        );

        // ================= SEARCH PANEL =================

        JPanel searchPanel = new JPanel(
                new BorderLayout(15, 0)
        );

        searchPanel.setBackground(CARD);
        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel searchLabel = new JLabel("Search Attendance");
        searchLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );
        searchLabel.setForeground(TEXT);

        searchField = new JTextField();
        searchField.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        searchField.setPreferredSize(
                new Dimension(300, 38)
        );

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

        JPanel searchInputPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        searchInputPanel.setOpaque(false);

        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        searchInputPanel.add(resetButton);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE CARD =================

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        String[] columns = {
                "ID",
                "Volunteer",
                "Event",
                "Date",
                "Check-In",
                "Check-Out",
                "Status",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(48);
        attendanceTable.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );
        attendanceTable.setForeground(TEXT);
        attendanceTable.setGridColor(BORDER);
        attendanceTable.setSelectionBackground(
                new Color(239, 246, 255)
        );
        attendanceTable.setSelectionForeground(TEXT);
        attendanceTable.setShowVerticalLines(false);

        attendanceTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        attendanceTable.getTableHeader().setForeground(TEXT);
        attendanceTable.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );
        attendanceTable.getTableHeader().setPreferredSize(
                new Dimension(0, 45)
        );

        // Column widths

        int[] widths = {
                60, 150, 220, 110, 100, 100, 100, 120
        };

        for (int i = 0; i < widths.length; i++) {
            attendanceTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        centerColumn(0);
        centerColumn(3);
        centerColumn(4);
        centerColumn(5);
        centerColumn(6);
        centerColumn(7);

        attendanceTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new StatusRenderer());

        attendanceTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(new ActionRenderer());

        attendanceTable.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2) {

                            int row =
                                    attendanceTable.getSelectedRow();

                            if (row != -1) {
                                showAttendanceDetails(row);
                            }
                        }
                    }
                }
        );

        JScrollPane scrollPane =
                new JScrollPane(attendanceTable);

        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================

        searchButton.addActionListener(
                e -> searchAttendance()
        );

        searchField.addActionListener(
                e -> searchAttendance()
        );

        resetButton.addActionListener(
                e -> {
                    searchField.setText("");
                    loadAttendanceFromDatabase();
                }
        );

        // Load actual database records

        loadAttendanceFromDatabase();
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
    // LOAD ATTENDANCE FROM MYSQL
    // =====================================================

    private void loadAttendanceFromDatabase() {

        tableModel.setRowCount(0);

        try {

            List<AttendanceDAO.AttendanceRecord> records =
                    attendanceService.getAllAttendance();

            for (AttendanceDAO.AttendanceRecord record : records) {

                addAttendanceRow(record);
            }

        } catch (Exception ex) {

            showError(
                    "Unable to load attendance records.\n" +
                            ex.getMessage()
            );
        }
    }

    // =====================================================
    // ADD ROW TO TABLE
    // =====================================================

    private void addAttendanceRow(
            AttendanceDAO.AttendanceRecord record
    ) {

        String formattedDate =
                record.getAttendanceDate()
                        .toLocalDate()
                        .format(displayFormatter);

        tableModel.addRow(
                new Object[]{
                        "A" + String.format(
                                "%03d",
                                record.getAttendanceId()
                        ),
                        record.getVolunteerName(),
                        record.getEventName(),
                        formattedDate,
                        "-",
                        "-",
                        record.getStatus(),
                        "View"
                }
        );
    }

    // =====================================================
    // MARK ATTENDANCE DIALOG
    // =====================================================

    private void showAttendanceDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Mark Attendance",
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

        // ================= TITLE =================

        JLabel title = new JLabel(
                "Mark Volunteer Attendance"
        );

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );

        title.setForeground(TEXT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(25));

        // ================= EVENT =================

        mainPanel.add(createLabel("Event"));

        JComboBox<AttendanceDAO.EventOption> eventBox =
                new JComboBox<>();

        styleComboBox(eventBox);
        mainPanel.add(eventBox);

        mainPanel.add(Box.createVerticalStrut(15));

        // ================= VOLUNTEER =================

        mainPanel.add(createLabel("Volunteer"));

        JComboBox<AttendanceDAO.VolunteerOption> volunteerBox =
                new JComboBox<>();

        styleComboBox(volunteerBox);
        volunteerBox.setEnabled(false);

        mainPanel.add(volunteerBox);

        mainPanel.add(Box.createVerticalStrut(15));

        // ================= DATE =================

        mainPanel.add(createLabel("Attendance Date"));

        JTextField dateField = new JTextField(
                LocalDate.now().toString()
        );

        dateField.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        dateField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40)
        );

        dateField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(0, 12, 0, 12)
                )
        );

        mainPanel.add(dateField);

        mainPanel.add(Box.createVerticalStrut(15));

        // ================= STATUS =================

        mainPanel.add(createLabel("Attendance Status"));

        JComboBox<String> statusBox =
                new JComboBox<>(
                        new String[]{
                                "Present",
                                "Absent",
                                "Late"
                        }
                );

        styleComboBox(statusBox);
        mainPanel.add(statusBox);

        mainPanel.add(Box.createVerticalStrut(25));

        // ================= BUTTONS =================

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
                "Save Attendance",
                PRIMARY,
                Color.WHITE
        );

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel);

        // ================= LOAD EVENTS =================

        try {

            List<AttendanceDAO.EventOption> events =
                    attendanceService.getAllEvents();

            for (AttendanceDAO.EventOption event : events) {
                eventBox.addItem(event);
            }

            if (events.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "No events found. Please create an event first.",
                        "No Events",
                        JOptionPane.INFORMATION_MESSAGE
                );

                saveButton.setEnabled(false);
            }

        } catch (Exception ex) {

            showError(
                    "Unable to load events.\n" +
                            ex.getMessage()
            );

            saveButton.setEnabled(false);
        }

        // ================= EVENT SELECTION =================

        eventBox.addActionListener(e -> {

            AttendanceDAO.EventOption selectedEvent =
                    (AttendanceDAO.EventOption)
                            eventBox.getSelectedItem();

            volunteerBox.removeAllItems();
            volunteerBox.setEnabled(false);

            if (selectedEvent == null) {
                return;
            }

            try {

                List<AttendanceDAO.VolunteerOption> volunteers =
                        attendanceService.getVolunteersByEvent(
                                selectedEvent.getEventId()
                        );

                for (AttendanceDAO.VolunteerOption volunteer
                        : volunteers) {

                    volunteerBox.addItem(volunteer);
                }

                volunteerBox.setEnabled(!volunteers.isEmpty());

                if (volunteers.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "No active volunteers are registered " +
                                    "for this event.",
                            "No Volunteers",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } catch (Exception ex) {

                showError(
                        "Unable to load volunteers.\n" +
                                ex.getMessage()
                );
            }
        });

        // ================= CANCEL =================

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        // ================= SAVE =================

        saveButton.addActionListener(e -> {

            AttendanceDAO.EventOption selectedEvent =
                    (AttendanceDAO.EventOption)
                            eventBox.getSelectedItem();

            AttendanceDAO.VolunteerOption selectedVolunteer =
                    (AttendanceDAO.VolunteerOption)
                            volunteerBox.getSelectedItem();

            String status =
                    (String) statusBox.getSelectedItem();

            if (selectedEvent == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select an event."
                );

                return;
            }

            if (selectedVolunteer == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a volunteer."
                );

                return;
            }

            Date attendanceDate;

            try {

                LocalDate parsedDate =
                        LocalDate.parse(dateField.getText().trim());

                if (parsedDate.isAfter(LocalDate.now())) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "Attendance date cannot be in the future."
                    );
                    return;
                }

                attendanceDate = Date.valueOf(parsedDate);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Invalid date. Use YYYY-MM-DD format.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            try {

                boolean saved =
                        attendanceService.markAttendance(
                                selectedVolunteer.getVolunteerId(),
                                selectedEvent.getEventId(),
                                status,
                                attendanceDate
                        );

                if (saved) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Attendance marked successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dialog.dispose();

                    searchField.setText("");
                    loadAttendanceFromDatabase();

                } else {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Attendance could not be saved."
                    );
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        ex.getMessage(),
                        "Attendance Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // =====================================================
    // STYLE COMBO BOX
    // =====================================================

    private void styleComboBox(JComboBox<?> comboBox) {

        comboBox.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        comboBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40)
        );
    }

    // =====================================================
    // CREATE LABEL
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
    // SEARCH ATTENDANCE
    // =====================================================

    private void searchAttendance() {

        String searchText =
                searchField.getText().trim();

        tableModel.setRowCount(0);

        try {

            List<AttendanceDAO.AttendanceRecord> records =
                    attendanceService.searchAttendance(searchText);

            for (AttendanceDAO.AttendanceRecord record : records) {
                addAttendanceRow(record);
            }

            if (records.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No attendance records found.",
                        "Search Result",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {

            showError(
                    "Unable to search attendance.\n" +
                            ex.getMessage()
            );
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

        attendanceTable.getColumnModel()
                .getColumn(column)
                .setCellRenderer(renderer);
    }

    // =====================================================
    // ATTENDANCE DETAILS
    // =====================================================

    private void showAttendanceDetails(int row) {

        String id =
                tableModel.getValueAt(row, 0).toString();

        String volunteer =
                tableModel.getValueAt(row, 1).toString();

        String event =
                tableModel.getValueAt(row, 2).toString();

        String date =
                tableModel.getValueAt(row, 3).toString();

        String checkIn =
                tableModel.getValueAt(row, 4).toString();

        String checkOut =
                tableModel.getValueAt(row, 5).toString();

        String status =
                tableModel.getValueAt(row, 6).toString();

        String message =
                "Attendance ID: " + id +
                        "\nVolunteer: " + volunteer +
                        "\nEvent: " + event +
                        "\nDate: " + date +
                        "\nCheck-In: " + checkIn +
                        "\nCheck-Out: " + checkOut +
                        "\nStatus: " + status;

        JOptionPane.showMessageDialog(
                this,
                message,
                "Attendance Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // ERROR MESSAGE
    // =====================================================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
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
                    value == null ? "" : value.toString()
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setOpaque(true);

            label.setFont(
                    new Font("Segoe UI", Font.BOLD, 12)
            );

            if ("Present".equals(value)) {

                label.setForeground(SUCCESS);
                label.setBackground(
                        new Color(220, 252, 231)
                );

            } else if ("Late".equals(value)) {

                label.setForeground(
                        new Color(180, 83, 9)
                );

                label.setBackground(
                        new Color(254, 243, 199)
                );

            } else {

                label.setForeground(DANGER);
                label.setBackground(
                        new Color(254, 226, 226)
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
                    value == null ? "" : value.toString()
            );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    new Font("Segoe UI", Font.BOLD, 12)
            );

            label.setForeground(PRIMARY);

            return label;
        }
    }
}
