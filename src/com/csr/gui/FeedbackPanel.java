
package com.csr.gui;

import com.csr.dao.FeedbackDAO;
import com.csr.dao.FeedbackDAO.EventOption;
import com.csr.dao.FeedbackDAO.FeedbackRecord;
import com.csr.dao.FeedbackDAO.VolunteerOption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class FeedbackPanel extends JPanel {

    // ================= COLORS =================

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color SECONDARY = new Color(100, 116, 139);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color WHITE = Color.WHITE;

    private final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 28);

    private final Font SUBTITLE_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    private final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    private final Font BOLD_FONT =
            new Font("Segoe UI", Font.BOLD, 13);

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    private JTable feedbackTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public FeedbackPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadFeedbackData();
    }

    // ================= HEADER =================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(25, 30, 15, 30));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Feedback");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Collect and review feedback from volunteers and participants"
        );

        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton addButton = new JButton("+ Add Feedback");
        stylePrimaryButton(addButton);

        addButton.addActionListener(
                e -> showAddFeedbackDialog()
        );

        header.add(titlePanel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        return header;
    }

    // ================= CONTENT =================

    private JPanel createContent() {

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(0, 30, 25, 30));

        content.add(createSearchPanel(), BorderLayout.NORTH);
        content.add(createTablePanel(), BorderLayout.CENTER);

        return content;
    }

    // ================= SEARCH =================

    private JPanel createSearchPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(12, 15, 12, 15)
                )
        );

        JPanel leftPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        leftPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(BOLD_FONT);
        searchLabel.setForeground(TEXT);

        searchField = new JTextField(25);
        searchField.setFont(NORMAL_FONT);
        searchField.setPreferredSize(new Dimension(250, 35));

        JButton searchButton = new JButton("Search");
        styleSecondaryButton(searchButton);

        JButton resetButton = new JButton("Reset");
        styleSecondaryButton(resetButton);

        searchButton.addActionListener(
                e -> searchFeedback(searchField.getText())
        );

        searchField.addActionListener(
                e -> searchFeedback(searchField.getText())
        );

        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadFeedbackData();
        });

        leftPanel.add(searchLabel);
        leftPanel.add(searchField);
        leftPanel.add(searchButton);
        leftPanel.add(resetButton);

        panel.add(leftPanel, BorderLayout.WEST);

        return panel;
    }

    // ================= TABLE =================

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));

        String[] columns = {
                "ID",
                "Volunteer",
                "Event",
                "Rating",
                "Feedback",
                "Date",
                "Status",
                "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        feedbackTable = new JTable(tableModel);

        feedbackTable.setFont(NORMAL_FONT);
        feedbackTable.setRowHeight(45);
        feedbackTable.setSelectionBackground(
                new Color(239, 246, 255)
        );
        feedbackTable.setSelectionForeground(TEXT);
        feedbackTable.setGridColor(BORDER);

        feedbackTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        feedbackTable.getTableHeader().setFont(BOLD_FONT);
        feedbackTable.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );
        feedbackTable.getTableHeader().setForeground(TEXT);
        feedbackTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );

        // Center alignment

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        feedbackTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(centerRenderer);

        feedbackTable.getColumnModel()
                .getColumn(3)
                .setCellRenderer(centerRenderer);

        feedbackTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(centerRenderer);

        feedbackTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new StatusRenderer());

        feedbackTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(centerRenderer);

        // Column widths

        feedbackTable.getColumnModel()
                .getColumn(0).setPreferredWidth(50);

        feedbackTable.getColumnModel()
                .getColumn(1).setPreferredWidth(130);

        feedbackTable.getColumnModel()
                .getColumn(2).setPreferredWidth(160);

        feedbackTable.getColumnModel()
                .getColumn(3).setPreferredWidth(70);

        feedbackTable.getColumnModel()
                .getColumn(4).setPreferredWidth(300);

        feedbackTable.getColumnModel()
                .getColumn(5).setPreferredWidth(100);

        feedbackTable.getColumnModel()
                .getColumn(6).setPreferredWidth(100);

        feedbackTable.getColumnModel()
                .getColumn(7).setPreferredWidth(80);

        // Double-click to view details

        feedbackTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int viewRow = feedbackTable.getSelectedRow();

                    if (viewRow != -1) {

                        int modelRow =
                                feedbackTable.convertRowIndexToModel(
                                        viewRow
                                );

                        showFeedbackDetails(modelRow);
                    }
                }
            }
        });

        JScrollPane scrollPane =
                new JScrollPane(feedbackTable);

        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ================= LOAD DATABASE DATA =================

    private void loadFeedbackData() {

        try {

            tableModel.setRowCount(0);

            List<FeedbackRecord> feedbackList =
                    feedbackDAO.getAllFeedback();

            for (FeedbackRecord feedback : feedbackList) {
                addFeedbackRow(feedback);
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load feedback records.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= ADD TABLE ROW =================

    private void addFeedbackRow(FeedbackRecord feedback) {

        tableModel.addRow(new Object[]{

                feedback.getFeedbackId(),

                feedback.getVolunteerName(),

                feedback.getEventName(),

                feedback.getRating() + " / 5",

                feedback.getComments(),

                "Not stored",

                "Submitted",

                "View"
        });
    }

    // ================= ADD FEEDBACK =================

    private void showAddFeedbackDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add Feedback",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ================= VOLUNTEER DROPDOWN =================

        JComboBox<VolunteerOption> volunteerBox =
                new JComboBox<>();

        try {

            List<VolunteerOption> volunteers =
                    feedbackDAO.getAllVolunteers();

            for (VolunteerOption volunteer : volunteers) {
                volunteerBox.addItem(volunteer);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load volunteers.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // ================= EVENT DROPDOWN =================

        JComboBox<EventOption> eventBox =
                new JComboBox<>();

        try {

            List<EventOption> events =
                    feedbackDAO.getAllEvents();

            for (EventOption event : events) {
                eventBox.addItem(event);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load events.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // ================= RATING =================

        JComboBox<Integer> ratingBox =
                new JComboBox<>(
                        new Integer[]{5, 4, 3, 2, 1}
                );

        // ================= COMMENTS =================

        JTextArea feedbackArea = new JTextArea(5, 25);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);

        // ================= FORM FIELDS =================

        addFormField(
                form, gbc, 0,
                "Volunteer:",
                volunteerBox
        );

        addFormField(
                form, gbc, 1,
                "Event:",
                eventBox
        );

        addFormField(
                form, gbc, 2,
                "Rating:",
                ratingBox
        );

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setFont(BOLD_FONT);
        feedbackLabel.setForeground(TEXT);

        form.add(feedbackLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane feedbackScroll =
                new JScrollPane(feedbackArea);

        form.add(feedbackScroll, gbc);

        // ================= BUTTONS =================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setBackground(WHITE);

        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Feedback");

        styleSecondaryButton(cancelButton);
        stylePrimaryButton(saveButton);

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        saveButton.addActionListener(e -> {

            VolunteerOption selectedVolunteer =
                    (VolunteerOption) volunteerBox.getSelectedItem();

            EventOption selectedEvent =
                    (EventOption) eventBox.getSelectedItem();

            Integer selectedRating =
                    (Integer) ratingBox.getSelectedItem();

            String comments =
                    feedbackArea.getText().trim();

            if (selectedVolunteer == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a volunteer.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (selectedEvent == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select an event.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (comments.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter feedback.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try {

                feedbackDAO.addFeedback(
                        selectedVolunteer.getVolunteerId(),
                        selectedEvent.getEventId(),
                        selectedRating,
                        comments
                );

                JOptionPane.showMessageDialog(
                        dialog,
                        "Feedback submitted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();

                loadFeedbackData();

            } catch (SQLException ex) {

                ex.printStackTrace();

                String message = ex.getMessage() == null
                        ? ""
                        : ex.getMessage().toLowerCase();

                if (message.contains("duplicate")
                        || message.contains("unique")) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "This volunteer has already submitted "
                                    + "feedback for this event.",
                            "Duplicate Feedback",
                            JOptionPane.WARNING_MESSAGE
                    );

                } else {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Unable to save feedback.\n"
                                    + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        ex.getMessage(),
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Unexpected error.\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ================= FORM FIELD =================

    private void addFormField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            JComponent component
    ) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel(labelText);
        label.setFont(BOLD_FONT);
        label.setForeground(TEXT);

        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        component.setFont(NORMAL_FONT);

        panel.add(component, gbc);
    }

    // ================= SEARCH =================

    private void searchFeedback(String keyword) {

        try {

            tableModel.setRowCount(0);

            List<FeedbackRecord> feedbackList =
                    feedbackDAO.searchFeedback(keyword);

            for (FeedbackRecord feedback : feedbackList) {
                addFeedbackRow(feedback);
            }

            if (feedbackList.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No feedback records found.",
                        "Search Result",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to search feedback.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= DETAILS =================

    private void showFeedbackDetails(int row) {

        String message =
                "Feedback ID: "
                        + tableModel.getValueAt(row, 0)
                        + "\nVolunteer: "
                        + tableModel.getValueAt(row, 1)
                        + "\nEvent: "
                        + tableModel.getValueAt(row, 2)
                        + "\nRating: "
                        + tableModel.getValueAt(row, 3)
                        + "\nDate: "
                        + tableModel.getValueAt(row, 5)
                        + "\nStatus: "
                        + tableModel.getValueAt(row, 6)
                        + "\n\nFeedback:\n"
                        + tableModel.getValueAt(row, 4);

        JOptionPane.showMessageDialog(
                this,
                message,
                "Feedback Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= BUTTON STYLES =================

    private void stylePrimaryButton(JButton button) {

        button.setFont(BOLD_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setPreferredSize(
                new Dimension(160, 38)
        );
    }

    private void styleSecondaryButton(JButton button) {

        button.setFont(NORMAL_FONT);
        button.setForeground(TEXT);
        button.setBackground(WHITE);
        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        button.setPreferredSize(
                new Dimension(90, 35)
        );
    }

    // ================= STATUS RENDERER =================

    private static class StatusRenderer
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

            JLabel label = (JLabel)
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setOpaque(true);
            label.setFont(
                    new Font("Segoe UI", Font.BOLD, 12)
            );

            String status = value == null
                    ? ""
                    : value.toString();

            if (status.equalsIgnoreCase("Submitted")) {

                label.setForeground(SUCCESS);
                label.setBackground(
                        new Color(220, 252, 231)
                );

            } else {

                label.setForeground(WARNING);
                label.setBackground(
                        new Color(254, 243, 199)
                );
            }

            if (isSelected) {
                label.setBorder(
                        BorderFactory.createLineBorder(
                                new Color(191, 219, 254)
                        )
                );
            }

            return label;
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(
                    "CSR Activity Management System"
            );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(1200, 750);
            frame.setLocationRelativeTo(null);

            frame.add(new FeedbackPanel());

            frame.setVisible(true);
        });
    }
}
