
package com.csr.gui;

import com.csr.dao.AttendanceReportDAO;
import com.csr.service.AttendanceReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AttendanceReportPanel extends JPanel {

    // ================= COLORS =================

    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color SECONDARY = new Color(100, 116, 139);
    private final Color BORDER = new Color(226, 232, 240);
    private final Color SUCCESS = new Color(22, 163, 74);
    private final Color DANGER = new Color(220, 38, 38);
    private final Color WARNING = new Color(217, 119, 6);

    private final AttendanceReportService reportService;

    private JLabel totalValue;
    private JLabel presentValue;
    private JLabel absentValue;
    private JLabel lateValue;
    private JLabel percentageValue;

    private JTable reportTable;
    private DefaultTableModel tableModel;

    // ================= CONSTRUCTOR =================

    public AttendanceReportPanel() {

        reportService = new AttendanceReportService();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Attendance Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Monitor volunteer participation and event attendance"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton refreshButton = createButton(
                "Refresh Reports",
                PRIMARY,
                Color.WHITE
        );

        refreshButton.addActionListener(
                e -> loadReports()
        );

        header.add(titlePanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ================= MAIN CONTENT =================

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.setBorder(
                new EmptyBorder(25, 0, 0, 0)
        );

        // ================= STATISTICS CARDS =================

        JPanel statsPanel = new JPanel(
                new GridLayout(1, 4, 15, 0)
        );
        statsPanel.setOpaque(false);

        totalValue = new JLabel("0");
        presentValue = new JLabel("0");
        absentValue = new JLabel("0");
        lateValue = new JLabel("0");

        statsPanel.add(createStatCard(
                "Total Records", totalValue, PRIMARY
        ));

        statsPanel.add(createStatCard(
                "Present", presentValue, SUCCESS
        ));

        statsPanel.add(createStatCard(
                "Absent", absentValue, DANGER
        ));

        statsPanel.add(createStatCard(
                "Late", lateValue, WARNING
        ));

        // ================= PERCENTAGE CARD =================

        JPanel percentageCard = new JPanel(new BorderLayout(10, 5));
        percentageCard.setBackground(CARD);
        percentageCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );

        JLabel percentageTitle = new JLabel(
                "Overall Attendance Rate"
        );

        percentageTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );

        percentageTitle.setForeground(TEXT);

        percentageValue = new JLabel("0.00%");
        percentageValue.setFont(
                new Font("Segoe UI", Font.BOLD, 30)
        );
        percentageValue.setForeground(PRIMARY);

        JLabel percentageDescription = new JLabel(
                "Percentage of recorded attendance marked Present or Late"
        );

        percentageDescription.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        percentageDescription.setForeground(SECONDARY);

        JPanel percentageInfo = new JPanel();
        percentageInfo.setOpaque(false);
        percentageInfo.setLayout(
                new BoxLayout(percentageInfo, BoxLayout.Y_AXIS)
        );

        percentageInfo.add(percentageTitle);
        percentageInfo.add(Box.createVerticalStrut(8));
        percentageInfo.add(percentageValue);
        percentageInfo.add(Box.createVerticalStrut(5));
        percentageInfo.add(percentageDescription);

        percentageCard.add(
                percentageInfo,
                BorderLayout.CENTER
        );

        JPanel topContent = new JPanel(
                new BorderLayout(0, 15)
        );
        topContent.setOpaque(false);

        topContent.add(statsPanel, BorderLayout.NORTH);
        topContent.add(percentageCard, BorderLayout.CENTER);

        content.add(topContent, BorderLayout.NORTH);

        // ================= EVENT-WISE REPORT TABLE =================

        JPanel tableCard = new JPanel(new BorderLayout());

        tableCard.setBackground(CARD);
        tableCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel tableTitle = new JLabel(
                "Event-wise Attendance Summary"
        );

        tableTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );

        tableTitle.setForeground(TEXT);

        tableTitle.setBorder(
                new EmptyBorder(0, 0, 15, 0)
        );

        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {
                "Event ID",
                "Event Name",
                "Total",
                "Present",
                "Absent",
                "Late",
                "Attendance %"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);

        reportTable.setRowHeight(45);
        reportTable.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        reportTable.setForeground(TEXT);
        reportTable.setGridColor(BORDER);
        reportTable.setShowVerticalLines(false);

        reportTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        reportTable.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );

        reportTable.getTableHeader().setForeground(TEXT);

        reportTable.getTableHeader().setPreferredSize(
                new Dimension(0, 42)
        );

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        content.add(tableCard, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // Load live reports
        loadReports();
    }

    // ================= STATISTICS CARD =================

    private JPanel createStatCard(
            String heading,
            JLabel valueLabel,
            Color accent
    ) {

        JPanel card = new JPanel(new BorderLayout(0, 12));

        card.setBackground(CARD);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(18, 18, 18, 18)
                )
        );

        JLabel headingLabel = new JLabel(heading);

        headingLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        headingLabel.setForeground(SECONDARY);

        valueLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 28)
        );

        valueLabel.setForeground(accent);

        card.add(headingLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ================= BUTTON =================

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
                new EmptyBorder(11, 18, 11, 18)
        );

        return button;
    }

    // ================= LOAD REPORTS =================

    private void loadReports() {

        try {

            // Load overall summary

            AttendanceReportDAO.AttendanceSummary summary =
                    reportService.getOverallSummary();

            totalValue.setText(
                    String.valueOf(summary.getTotal())
            );

            presentValue.setText(
                    String.valueOf(summary.getPresent())
            );

            absentValue.setText(
                    String.valueOf(summary.getAbsent())
            );

            lateValue.setText(
                    String.valueOf(summary.getLate())
            );

            percentageValue.setText(
                    String.format(
                            "%.2f%%",
                            summary.getPercentage()
                    )
            );

            // Load event-wise summaries

            tableModel.setRowCount(0);

            List<AttendanceReportDAO.EventSummary> summaries =
                    reportService.getEventSummaries();

            for (AttendanceReportDAO.EventSummary event : summaries) {

                tableModel.addRow(
                        new Object[]{
                                event.getEventId(),
                                event.getEventName(),
                                event.getTotal(),
                                event.getPresent(),
                                event.getAbsent(),
                                event.getLate(),
                                String.format(
                                        "%.2f%%",
                                        event.getPercentage()
                                )
                        }
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load attendance reports.\n" +
                            ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
