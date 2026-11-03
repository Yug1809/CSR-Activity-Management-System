
package com.csr.gui;

import com.csr.dao.EventDAO;
import com.csr.dao.ReportDAO;
import com.csr.dao.ReportHistoryDAO;
import com.csr.service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportPanel extends JPanel {

    // ================= COLORS =================

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color PRIMARY =
            new Color(37, 99, 235);

    private static final Color TEXT =
            new Color(30, 41, 59);

    private static final Color SECONDARY =
            new Color(100, 116, 139);

    private static final Color SUCCESS =
            new Color(22, 163, 74);

    private static final Color WARNING =
            new Color(245, 158, 11);

    private static final Color DANGER =
            new Color(220, 38, 38);

    private static final Color BORDER =
            new Color(226, 232, 240);

    private static final Color WHITE = Color.WHITE;

    // ================= FONTS =================

    private final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 28);

    private final Font SUBTITLE_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    private final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    private final Font BOLD_FONT =
            new Font("Segoe UI", Font.BOLD, 13);

    // ================= COMPONENTS =================

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JLabel totalReportsValue;
    private JLabel recordsValue;
    private JLabel eventsValue;
    private JLabel lastGeneratedValue;

    // ================= SERVICES =================

    private final ReportService reportService =
            new ReportService();

    private final ReportHistoryDAO historyDAO =
            new ReportHistoryDAO();

    // ================= CURRENT REPORT =================

    private ReportDAO.ReportData currentReport;

    private String currentReportType;
    private String currentEventName;

    private Integer currentEventId;

    private int currentHistoryId = -1;

    // ================= CONSTRUCTOR =================

    public ReportPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadReportHistory();
    }

    // ================= HEADER =================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(BACKGROUND);

        header.setBorder(
                new EmptyBorder(25, 30, 15, 30)
        );

        JPanel titlePanel = new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Reports");

        title.setFont(TITLE_FONT);
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Generate and monitor reports for CSR activities"
        );

        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton generateButton =
                new JButton("+ Generate Report");

        stylePrimaryButton(generateButton);

        generateButton.addActionListener(
                e -> showGenerateReportDialog()
        );

        header.add(titlePanel, BorderLayout.WEST);
        header.add(generateButton, BorderLayout.EAST);

        return header;
    }

    // ================= CONTENT =================

    private JPanel createContent() {

        JPanel content =
                new JPanel(new BorderLayout(0, 15));

        content.setBackground(BACKGROUND);

        content.setBorder(
                new EmptyBorder(0, 30, 25, 30)
        );

        content.add(
                createSummaryCards(),
                BorderLayout.NORTH
        );

        content.add(
                createTablePanel(),
                BorderLayout.CENTER
        );

        return content;
    }

    // ================= SUMMARY CARDS =================

    private JPanel createSummaryCards() {

        JPanel cards = new JPanel(
                new GridLayout(1, 4, 15, 0)
        );

        cards.setBackground(BACKGROUND);

        JPanel card1 = createSummaryCard(
                "Reports Generated", "0", PRIMARY
        );

        JPanel card2 = createSummaryCard(
                "Records in Last Report", "0", SUCCESS
        );

        JPanel card3 = createSummaryCard(
                "Events in Last Report", "0", WARNING
        );

        JPanel card4 = createSummaryCard(
                "Last Generated", "-", DANGER
        );

        totalReportsValue = getCardValue(card1);
        recordsValue = getCardValue(card2);
        eventsValue = getCardValue(card3);
        lastGeneratedValue = getCardValue(card4);

        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);

        return cards;
    }

    private JPanel createSummaryCard(
            String title,
            String value,
            Color accent) {

        JPanel card = new JPanel(
                new BorderLayout()
        );

        card.setBackground(WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 18, 15, 18)
                )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(NORMAL_FONT);
        titleLabel.setForeground(SECONDARY);

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 25)
        );

        valueLabel.setForeground(TEXT);

        JPanel top = new JPanel(new BorderLayout());

        top.setOpaque(false);

        top.add(titleLabel, BorderLayout.WEST);

        JLabel indicator = new JLabel("●");

        indicator.setForeground(accent);

        top.add(indicator, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel getCardValue(JPanel card) {

        for (Component component : card.getComponents()) {

            if (component instanceof JLabel label
                    && label.getFont().getSize() == 25) {
                return label;
            }
        }

        return null;
    }

    // ================= TABLE =================

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(WHITE);

        panel.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        String[] columns = {
                "ID",
                "Report Name",
                "Report Type",
                "Event",
                "Generated On",
                "Generated By",
                "Status",
                "Action"
        };

        tableModel = new DefaultTableModel(
                columns,
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);

        reportTable.setFont(NORMAL_FONT);
        reportTable.setRowHeight(45);

        reportTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        reportTable.setSelectionForeground(TEXT);
        reportTable.setGridColor(BORDER);

        reportTable.getTableHeader().setFont(BOLD_FONT);

        reportTable.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );

        reportTable.getTableHeader().setForeground(TEXT);

        reportTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        reportTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(centerRenderer);

        reportTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new StatusRenderer());

        reportTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(centerRenderer);

        int[] widths = {
                50, 180, 120, 180,
                150, 120, 100, 100
        };

        for (int i = 0; i < widths.length; i++) {

            reportTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        // Double-click to open exported report

        reportTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        if (e.getClickCount() == 2) {

                            int row =
                                    reportTable.getSelectedRow();

                            if (row >= 0) {
                                openSavedReport(row);
                            }
                        }
                    }
                }
        );

        JScrollPane scrollPane =
                new JScrollPane(reportTable);

        scrollPane.setBorder(null);

        panel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ================= EVENT CHOICE =================

    private static class EventChoice {

        private final Integer eventId;
        private final String eventName;

        EventChoice(Integer eventId, String eventName) {

            this.eventId = eventId;
            this.eventName = eventName;
        }

        @Override
        public String toString() {
            return eventName;
        }
    }

    // ================= GENERATE REPORT =================

    private void showGenerateReportDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Generate Report",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(
                new GridBagLayout()
        );

        form.setBackground(WHITE);

        form.setBorder(
                new EmptyBorder(20, 25, 20, 25)
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Report type

        JLabel typeLabel = new JLabel("Report Type:");

        typeLabel.setFont(BOLD_FONT);
        typeLabel.setForeground(TEXT);

        JComboBox<String> typeBox =
                new JComboBox<>(
                        new String[]{
                                "Event Report",
                                "Volunteer Report",
                                "Attendance Report",
                                "Budget Report",
                                "Feedback Report",
                                "Complete CSR Report"
                        }
                );

        typeBox.setFont(NORMAL_FONT);

        // Event dropdown

        JLabel eventLabel = new JLabel("Event:");

        eventLabel.setFont(BOLD_FONT);
        eventLabel.setForeground(TEXT);

        JComboBox<EventChoice> eventBox =
                new JComboBox<>();

        eventBox.setFont(NORMAL_FONT);

        eventBox.addItem(
                new EventChoice(null, "All Events")
        );

        try {

            List<EventDAO.EventRecord> events =
                    reportService.getAllEvents();

            for (EventDAO.EventRecord event : events) {

                eventBox.addItem(
                        new EventChoice(
                                event.id,
                                event.name
                        )
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    dialog,
                    "Unable to load events:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Format dropdown

        JLabel formatLabel = new JLabel("Format:");

        formatLabel.setFont(BOLD_FONT);
        formatLabel.setForeground(TEXT);

        JComboBox<String> formatBox =
                new JComboBox<>(
                        new String[]{
                                "CSV",
                                "Excel-compatible CSV"
                        }
                );

        formatBox.setFont(NORMAL_FONT);

        // Form layout

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        form.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(typeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        form.add(eventLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(eventBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        form.add(formatLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(formatBox, gbc);

        // Buttons

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttons.setBackground(WHITE);

        JButton cancelButton =
                new JButton("Cancel");

        JButton generateButton =
                new JButton("Generate");

        styleSecondaryButton(cancelButton);
        stylePrimaryButton(generateButton);

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        generateButton.addActionListener(e -> {

            String type =
                    typeBox.getSelectedItem().toString();

            EventChoice selectedEvent =
                    (EventChoice) eventBox.getSelectedItem();

            String format =
                    formatBox.getSelectedItem().toString();

            Integer eventId = selectedEvent == null
                    ? null : selectedEvent.eventId;

            try {

                // Generate actual database report

                ReportDAO.ReportData data =
                        reportService.generateReport(
                                type,
                                eventId
                        );

                if (data.getRows().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "No records found for this report.",
                            "No Data",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    return;
                }

                currentReport = data;
                currentReportType = type;

                currentEventId = eventId;

                currentEventName = selectedEvent == null
                        ? "All Events"
                        : selectedEvent.eventName;

                // Save report history in MySQL

                currentHistoryId =
                        historyDAO.saveReport(
                                type,
                                type,
                                eventId,
                                format,
                                null,
                                data.getRows().size(),
                                null
                        );

                // Refresh persistent history

                loadReportHistory();

                dialog.dispose();

                // Preview generated report

                showReportPreview();

                // Export report

                exportCurrentReport();

            } catch (SQLException |
                     IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Report generation failed:\n"
                                + ex.getMessage(),
                        "Report Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        buttons.add(cancelButton);
        buttons.add(generateButton);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ================= LOAD REPORT HISTORY =================

    private void loadReportHistory() {

        tableModel.setRowCount(0);

        try {

            List<ReportHistoryDAO.ReportHistoryRecord> reports =
                    historyDAO.getAllReports();

            for (ReportHistoryDAO.ReportHistoryRecord report
                    : reports) {

                Timestamp timestamp =
                        report.getGeneratedAt();

                String generatedDate = timestamp == null
                        ? "-"
                        : timestamp.toLocalDateTime().format(
                        DateTimeFormatter.ofPattern(
                                "dd-MMM-yyyy HH:mm"
                        )
                );

                String status =
                        report.getFilePath() != null
                                && !report.getFilePath()
                                .trim().isEmpty()
                                ? "Exported"
                                : "Generated";

                tableModel.addRow(
                        new Object[]{
                                report.getReportId(),
                                report.getReportName(),
                                report.getReportType(),
                                report.getEventName(),
                                generatedDate,
                                "Not linked",
                                status,
                                report.getFilePath() == null
                                        ? "Not Exported"
                                        : "Open"
                        }
                );
            }

            totalReportsValue.setText(
                    String.valueOf(reports.size())
            );

            if (!reports.isEmpty()) {

                ReportHistoryDAO.ReportHistoryRecord latest =
                        reports.get(0);

                recordsValue.setText(
                        String.valueOf(latest.getRecordCount())
                );

                eventsValue.setText(
                        "All Events".equals(latest.getEventName())
                                ? "-"
                                : "1"
                );

                Timestamp timestamp =
                        latest.getGeneratedAt();

                lastGeneratedValue.setText(
                        timestamp == null
                                ? "-"
                                : timestamp.toLocalDateTime().format(
                                DateTimeFormatter.ofPattern(
                                        "dd MMM"
                                )
                        )
                );

            } else {

                recordsValue.setText("0");
                eventsValue.setText("0");
                lastGeneratedValue.setText("-");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load report history:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= REPORT PREVIEW =================

    private void showReportPreview() {

        if (currentReport == null) {
            return;
        }

        String[] columns =
                currentReport.getColumns();

        DefaultTableModel previewModel =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {
                        return false;
                    }
                };

        for (String[] row : currentReport.getRows()) {
            previewModel.addRow(row);
        }

        JTable previewTable =
                new JTable(previewModel);

        previewTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        previewTable.setRowHeight(28);

        JScrollPane scrollPane =
                new JScrollPane(previewTable);

        JDialog previewDialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                currentReportType + " Preview",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        previewDialog.setSize(1000, 550);
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setLayout(new BorderLayout());

        JLabel heading = new JLabel(
                "  " + currentReportType
                        + " | "
                        + currentReport.getRows().size()
                        + " records"
        );

        heading.setFont(BOLD_FONT);

        heading.setBorder(
                new EmptyBorder(12, 10, 12, 10)
        );

        JButton exportButton =
                new JButton("Export CSV");

        stylePrimaryButton(exportButton);

        exportButton.addActionListener(
                e -> exportCurrentReport()
        );

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        bottom.add(exportButton);

        previewDialog.add(heading, BorderLayout.NORTH);
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        previewDialog.add(bottom, BorderLayout.SOUTH);

        previewDialog.setVisible(true);
    }

    // ================= CSV EXPORT =================

    private void exportCurrentReport() {

        if (currentReport == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please generate a report first."
            );

            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Save Report");

        String safeName = currentReportType
                .replaceAll("[^a-zA-Z0-9]", "_");

        chooser.setSelectedFile(
                new File(safeName + ".csv")
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName()
                .toLowerCase()
                .endsWith(".csv")) {

            file = new File(
                    file.getAbsolutePath() + ".csv"
            );
        }

        try (FileWriter writer = new FileWriter(file)) {

            writeCsvRow(
                    writer,
                    currentReport.getColumns()
            );

            for (String[] row : currentReport.getRows()) {
                writeCsvRow(writer, row);
            }

            // Update saved file path in MySQL

            if (currentHistoryId > 0) {

                historyDAO.updateFilePath(
                        currentHistoryId,
                        file.getAbsolutePath()
                );
            }

            loadReportHistory();

            JOptionPane.showMessageDialog(
                    this,
                    "Report exported successfully!\n"
                            + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException | SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Export or history update failed:\n"
                            + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void writeCsvRow(
            FileWriter writer,
            String[] values) throws IOException {

        for (int i = 0; i < values.length; i++) {

            if (i > 0) {
                writer.append(",");
            }

            String value =
                    values[i] == null ? "" : values[i];

            value = value.replace("\"", "\"\"");

            writer.append("\"")
                    .append(value)
                    .append("\"");
        }

        writer.append(System.lineSeparator());
    }

    // ================= OPEN SAVED REPORT =================

    private void openSavedReport(int row) {

        Object idValue =
                tableModel.getValueAt(row, 0);

        if (idValue == null) {
            return;
        }

        int reportId =
                Integer.parseInt(idValue.toString());

        try {

            List<ReportHistoryDAO.ReportHistoryRecord> reports =
                    historyDAO.getAllReports();

            for (ReportHistoryDAO.ReportHistoryRecord report
                    : reports) {

                if (report.getReportId() == reportId) {

                    String path = report.getFilePath();

                    if (path == null || path.trim().isEmpty()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "This report has not been exported yet.",
                                "File Not Available",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        return;
                    }

                    File file = new File(path);

                    if (!file.exists()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "The exported file could not be found:\n"
                                        + path,
                                "File Not Found",
                                JOptionPane.ERROR_MESSAGE
                        );

                        return;
                    }

                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Opening files is not supported on this system."
                        );
                    }

                    return;
                }
            }

        } catch (SQLException | IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open report:\n"
                            + ex.getMessage(),
                    "Open Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= BUTTON STYLE =================

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
                new Dimension(170, 38)
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
                int column) {

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

            if ("Exported".equals(value)) {

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

            frame.add(new ReportPanel());

            frame.setVisible(true);
        });
    }
}
