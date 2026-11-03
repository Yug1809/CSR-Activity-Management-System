
package com.csr.gui;

import com.csr.dao.DashboardDAO;
import com.csr.dao.DashboardDAO.DashboardStats;
import com.csr.dao.DashboardDAO.RecentEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DashboardFrame extends JFrame {

    // =========================
    // COLORS
    // =========================

    private static final Color SIDEBAR_COLOR =
            new Color(30, 41, 59);

    private static final Color SIDEBAR_HOVER =
            new Color(51, 65, 85);

    private static final Color PRIMARY_COLOR =
            new Color(37, 99, 235);

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 250);

    private static final Color CARD_COLOR =
            Color.WHITE;

    private static final Color TEXT_COLOR =
            new Color(30, 41, 59);

    private static final Color SECONDARY_TEXT =
            new Color(100, 116, 139);

    // =========================
    // COMPONENTS
    // =========================

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JButton selectedButton;

    private JLabel totalEventsValue;
    private JLabel volunteersValue;
    private JLabel participantsValue;
    private JLabel budgetValue;

    private JPanel activityListPanel;
    private JLabel dashboardStatusLabel;

    private final DashboardDAO dashboardDAO =
            new DashboardDAO();

    // =========================
    // CONSTRUCTOR
    // =========================

    public DashboardFrame() {

        setTitle("CSR Activity Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createContent();

        cardLayout.show(contentPanel, "Dashboard");

        // Load actual database information
        refreshDashboardData();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private void createHeader() {

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(Color.WHITE);

        headerPanel.setPreferredSize(
                new Dimension(1200, 70)
        );

        headerPanel.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0,
                        new Color(220, 225, 230)
                )
        );

        JPanel leftHeader =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT, 20, 15
                ));

        leftHeader.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel("CSR");

        logoLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );

        logoLabel.setForeground(PRIMARY_COLOR);

        JLabel titleLabel =
                new JLabel("Activity Management System");

        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 18)
        );

        titleLabel.setForeground(TEXT_COLOR);

        leftHeader.add(logoLabel);
        leftHeader.add(titleLabel);

        JPanel rightHeader =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT, 15, 12
                ));

        rightHeader.setBackground(Color.WHITE);

        JButton notificationButton =
                createHeaderButton("Notifications");

        JButton profileButton =
                createHeaderButton("Administrator");

        JButton logoutButton =
                createHeaderButton("Logout");

        rightHeader.add(notificationButton);
        rightHeader.add(profileButton);
        rightHeader.add(logoutButton);

        headerPanel.add(
                leftHeader,
                BorderLayout.WEST
        );

        headerPanel.add(
                rightHeader,
                BorderLayout.EAST
        );

        add(headerPanel, BorderLayout.NORTH);

        logoutButton.addActionListener(e -> logout());
    }

    // =========================================================
    // HEADER BUTTON
    // =========================================================

    private JButton createHeaderButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private void createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());

        sidebar.setBackground(SIDEBAR_COLOR);

        sidebar.setPreferredSize(
                new Dimension(210, 680)
        );

        JPanel sidebarTop = new JPanel();

        sidebarTop.setLayout(
                new BoxLayout(
                        sidebarTop,
                        BoxLayout.Y_AXIS
                )
        );

        sidebarTop.setBackground(SIDEBAR_COLOR);

        sidebarTop.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 12, 15, 12
                )
        );

        JLabel menuLabel = new JLabel("MAIN MENU");

        menuLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        menuLabel.setForeground(
                new Color(148, 163, 184)
        );

        menuLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        sidebarTop.add(menuLabel);
        sidebarTop.add(Box.createVerticalStrut(12));

        JButton dashboardButton =
                createSidebarButton("Dashboard");

        JButton eventsButton =
                createSidebarButton("Events");

        JButton volunteersButton =
                createSidebarButton("Volunteers");

        JButton attendanceButton =
                createSidebarButton("Attendance");

        JButton attendanceAnalyticsButton =
                createSidebarButton("Attendance Analytics");

        JButton budgetButton =
                createSidebarButton("Budget");

        JButton feedbackButton =
                createSidebarButton("Feedback");

        JButton reportsButton =
                createSidebarButton("Reports");

        sidebarTop.add(dashboardButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(eventsButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(volunteersButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(attendanceButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(attendanceAnalyticsButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(budgetButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(feedbackButton);
        sidebarTop.add(Box.createVerticalStrut(5));

        sidebarTop.add(reportsButton);

        sidebar.add(
                sidebarTop,
                BorderLayout.NORTH
        );

        // Sidebar bottom

        JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(
                new BoxLayout(
                        bottomPanel,
                        BoxLayout.Y_AXIS
                )
        );

        bottomPanel.setBackground(SIDEBAR_COLOR);

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 12, 20, 12
                )
        );

        JLabel systemLabel =
                new JLabel("CSR Management System");

        systemLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        systemLabel.setForeground(
                new Color(148, 163, 184)
        );

        systemLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        bottomPanel.add(systemLabel);
        bottomPanel.add(Box.createVerticalStrut(5));

        JLabel versionLabel = new JLabel("Version 1.0");

        versionLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 10)
        );

        versionLabel.setForeground(
                new Color(100, 116, 139)
        );

        versionLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        bottomPanel.add(versionLabel);

        sidebar.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(sidebar, BorderLayout.WEST);

        // Navigation

        dashboardButton.addActionListener(e ->
                navigateTo("Dashboard", dashboardButton)
        );

        eventsButton.addActionListener(e ->
                navigateTo("Events", eventsButton)
        );

        volunteersButton.addActionListener(e ->
                navigateTo("Volunteers", volunteersButton)
        );

        attendanceButton.addActionListener(e ->
                navigateTo("Attendance", attendanceButton)
        );

        attendanceAnalyticsButton.addActionListener(e ->
                navigateTo(
                        "AttendanceAnalytics",
                        attendanceAnalyticsButton
                )
        );

        budgetButton.addActionListener(e ->
                navigateTo("Budget", budgetButton)
        );

        feedbackButton.addActionListener(e ->
                navigateTo("Feedback", feedbackButton)
        );

        reportsButton.addActionListener(e ->
                navigateTo("Reports", reportsButton)
        );

        selectButton(dashboardButton);
    }

    // =========================================================
    // NAVIGATION HELPER
    // =========================================================

    private void navigateTo(
            String cardName,
            JButton button
    ) {

        cardLayout.show(contentPanel, cardName);
        selectButton(button);
    }

    // =========================================================
    // SIDEBAR BUTTON
    // =========================================================

    private JButton createSidebarButton(String text) {

        JButton button = new JButton(text);

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45)
        );

        button.setPreferredSize(
                new Dimension(180, 45)
        );

        button.setMinimumSize(
                new Dimension(180, 45)
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        button.setForeground(
                new Color(226, 232, 240)
        );

        button.setBackground(SIDEBAR_COLOR);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 18, 0, 10
                )
        );

        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {

                        if (button != selectedButton) {
                            button.setBackground(SIDEBAR_HOVER);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                        if (button != selectedButton) {
                            button.setBackground(SIDEBAR_COLOR);
                        }
                    }
                }
        );

        return button;
    }

    // =========================================================
    // SELECT SIDEBAR BUTTON
    // =========================================================

    private void selectButton(JButton button) {

        if (selectedButton != null) {

            selectedButton.setBackground(SIDEBAR_COLOR);

            selectedButton.setForeground(
                    new Color(226, 232, 240)
            );
        }

        selectedButton = button;

        selectedButton.setBackground(PRIMARY_COLOR);
        selectedButton.setForeground(Color.WHITE);
    }

    // =========================================================
    // CONTENT AREA
    // =========================================================

    private void createContent() {

        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);

        contentPanel.setBackground(BACKGROUND_COLOR);

        JPanel dashboardPanel =
                createDashboardPanel();

        JPanel eventsPanel =
                new EventPanel();

        JPanel volunteersPanel =
                new VolunteerPanel();

        JPanel attendancePanel =
                new AttendancePanel();

        JPanel attendanceAnalyticsPanel =
                new AttendanceReportPanel();

        JPanel budgetPanel =
                new BudgetPanel();

        JPanel feedbackPanel =
                new FeedbackPanel();

        JPanel reportsPanel =
                new ReportPanel();

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(eventsPanel, "Events");
        contentPanel.add(volunteersPanel, "Volunteers");
        contentPanel.add(attendancePanel, "Attendance");

        contentPanel.add(
                attendanceAnalyticsPanel,
                "AttendanceAnalytics"
        );

        contentPanel.add(budgetPanel, "Budget");
        contentPanel.add(feedbackPanel, "Feedback");
        contentPanel.add(reportsPanel, "Reports");

        add(contentPanel, BorderLayout.CENTER);
    }

    // =========================================================
    // DASHBOARD PANEL
    // =========================================================

    private JPanel createDashboardPanel() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 20, 30
                )
        );

        // Top section

        JPanel topPanel = new JPanel();

        topPanel.setLayout(
                new BoxLayout(
                        topPanel,
                        BoxLayout.Y_AXIS
                )
        );

        topPanel.setBackground(BACKGROUND_COLOR);

        JPanel headingPanel =
                new JPanel(new BorderLayout());

        headingPanel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Dashboard");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 28)
        );

        title.setForeground(TEXT_COLOR);

        JButton refreshButton =
                new JButton("Refresh Dashboard");

        refreshButton.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setFocusPainted(false);

        refreshButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        refreshButton.addActionListener(e ->
                refreshDashboardData()
        );

        headingPanel.add(title, BorderLayout.WEST);
        headingPanel.add(refreshButton, BorderLayout.EAST);

        JLabel subtitle =
                new JLabel("Welcome back, Administrator");

        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        subtitle.setForeground(SECONDARY_TEXT);

        topPanel.add(headingPanel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subtitle);
        topPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center section

        JPanel centerPanel =
                new JPanel(new BorderLayout(0, 20));

        centerPanel.setBackground(BACKGROUND_COLOR);

        JPanel cardsPanel =
                new JPanel(
                        new GridLayout(1, 4, 15, 15)
                );

        cardsPanel.setBackground(BACKGROUND_COLOR);

        totalEventsValue = new JLabel("Loading...");
        volunteersValue = new JLabel("Loading...");
        participantsValue = new JLabel("Loading...");
        budgetValue = new JLabel("Loading...");

        cardsPanel.add(
                createModernCard(
                        "Total Events",
                        totalEventsValue,
                        "All registered CSR events"
                )
        );

        cardsPanel.add(
                createModernCard(
                        "Volunteers",
                        volunteersValue,
                        "Active volunteer accounts"
                )
        );

        cardsPanel.add(
                createModernCard(
                        "Participants",
                        participantsValue,
                        "Total event registrations"
                )
        );

        cardsPanel.add(
                createModernCard(
                        "Total Budget",
                        budgetValue,
                        "Total allocated budget"
                )
        );

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Recent activities

        JPanel activityPanel =
                createActivityPanel();

        centerPanel.add(
                activityPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom status

        JPanel statusPanel =
                new JPanel(new BorderLayout());

        statusPanel.setBackground(BACKGROUND_COLOR);

        JLabel loggedLabel =
                new JLabel("Logged in as: Administrator");

        loggedLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        loggedLabel.setForeground(SECONDARY_TEXT);

        dashboardStatusLabel =
                new JLabel("●  Loading dashboard...");

        dashboardStatusLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        dashboardStatusLabel.setForeground(
                new Color(22, 163, 74)
        );

        statusPanel.add(loggedLabel, BorderLayout.WEST);
        statusPanel.add(dashboardStatusLabel, BorderLayout.EAST);

        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // =========================================================
    // MODERN CARD
    // =========================================================

    private JPanel createModernCard(
            String title,
            JLabel valueLabel,
            String description
    ) {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(CARD_COLOR);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
                        ),
                        BorderFactory.createEmptyBorder(
                                18, 18, 18, 18
                        )
                )
        );

        JLabel titleLabel =
                new JLabel(title.toUpperCase());

        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        titleLabel.setForeground(SECONDARY_TEXT);

        valueLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 30)
        );

        valueLabel.setForeground(TEXT_COLOR);

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        descriptionLabel.setForeground(SECONDARY_TEXT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descriptionLabel, BorderLayout.SOUTH);

        return card;
    }

    // =========================================================
    // RECENT ACTIVITIES PANEL
    // =========================================================

    private JPanel createActivityPanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

        panel.setBackground(CARD_COLOR);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
                        ),
                        BorderFactory.createEmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        JLabel title =
                new JLabel("Recent Activities");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 18)
        );

        title.setForeground(TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        activityListPanel = new JPanel();

        activityListPanel.setLayout(
                new BoxLayout(
                        activityListPanel,
                        BoxLayout.Y_AXIS
                )
        );

        activityListPanel.setBackground(CARD_COLOR);

        activityListPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 0, 0, 0
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(activityListPanel);

        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================
    // REFRESH DASHBOARD DATA
    // =========================================================

    private void refreshDashboardData() {

        try {

            DashboardStats stats =
                    dashboardDAO.getDashboardStats();

            totalEventsValue.setText(
                    String.valueOf(stats.getTotalEvents())
            );

            volunteersValue.setText(
                    String.valueOf(stats.getTotalVolunteers())
            );

            participantsValue.setText(
                    String.valueOf(stats.getTotalParticipants())
            );

            budgetValue.setText(
                    formatCurrency(stats.getTotalBudget())
            );

            loadRecentEvents();

            dashboardStatusLabel.setText(
                    "●  Dashboard updated successfully"
            );

            dashboardStatusLabel.setForeground(
                    new Color(22, 163, 74)
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            totalEventsValue.setText("N/A");
            volunteersValue.setText("N/A");
            participantsValue.setText("N/A");
            budgetValue.setText("N/A");

            activityListPanel.removeAll();

            JLabel errorLabel =
                    new JLabel("Unable to load dashboard data.");

            errorLabel.setForeground(Color.RED);

            activityListPanel.add(errorLabel);

            activityListPanel.revalidate();
            activityListPanel.repaint();

            dashboardStatusLabel.setText(
                    "●  Database connection error"
            );

            dashboardStatusLabel.setForeground(Color.RED);
        }
    }

    // =========================================================
    // LOAD RECENT EVENTS FROM MYSQL
    // =========================================================

    private void loadRecentEvents() throws Exception {

        activityListPanel.removeAll();

        List<RecentEvent> events =
                dashboardDAO.getRecentEvents();

        if (events.isEmpty()) {

            JLabel emptyLabel =
                    new JLabel("No events found.");

            emptyLabel.setFont(
                    new Font("Segoe UI", Font.PLAIN, 13)
            );

            emptyLabel.setForeground(SECONDARY_TEXT);

            activityListPanel.add(emptyLabel);

        } else {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("dd MMM yyyy");

            for (RecentEvent event : events) {

                String eventDate =
                        event.getEventDate() == null
                                ? "Date not available"
                                : dateFormat.format(
                                event.getEventDate()
                        );

                activityListPanel.add(
                        createActivityRow(
                                event.getEventName(),
                                eventDate,
                                event.getStatus()
                        )
                );

                activityListPanel.add(
                        Box.createVerticalStrut(8)
                );
            }
        }

        activityListPanel.revalidate();
        activityListPanel.repaint();
    }

    // =========================================================
    // CURRENCY FORMAT
    // =========================================================

    private String formatCurrency(BigDecimal amount) {

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        DecimalFormat formatter =
                new DecimalFormat("#,##,##0.00");

        return "₹" + formatter.format(amount);
    }

    // =========================================================
    // ACTIVITY ROW
    // =========================================================

    private JPanel createActivityRow(
            String event,
            String date,
            String status
    ) {

        JPanel row =
                new JPanel(new BorderLayout());

        row.setBackground(
                new Color(248, 250, 252)
        );

        row.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 15, 12, 15
                )
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        65
                )
        );

        JLabel eventLabel = new JLabel(event);

        eventLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        eventLabel.setForeground(TEXT_COLOR);

        JLabel dateLabel = new JLabel(date);

        dateLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        dateLabel.setForeground(SECONDARY_TEXT);

        JPanel left = new JPanel();

        left.setLayout(
                new BoxLayout(
                        left,
                        BoxLayout.Y_AXIS
                )
        );

        left.setBackground(
                new Color(248, 250, 252)
        );

        left.add(eventLabel);
        left.add(Box.createVerticalStrut(3));
        left.add(dateLabel);

        JLabel statusLabel =
                new JLabel(status == null ? "Unknown" : status);

        statusLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        if ("Completed".equalsIgnoreCase(status)) {

            statusLabel.setForeground(
                    new Color(22, 163, 74)
            );

        } else if ("Cancelled".equalsIgnoreCase(status)) {

            statusLabel.setForeground(
                    new Color(220, 38, 38)
            );

        } else if ("Ongoing".equalsIgnoreCase(status)) {

            statusLabel.setForeground(
                    new Color(37, 99, 235)
            );

        } else {

            statusLabel.setForeground(
                    new Color(245, 158, 11)
            );
        }

        row.add(left, BorderLayout.WEST);
        row.add(statusLabel, BorderLayout.EAST);

        return row;
    }

    // =========================================================
    // TEMPORARY PANELS
    // =========================================================

    private JPanel createTemporaryPanel(String title) {

        JPanel panel =
                new JPanel(new BorderLayout());

        panel.setBackground(BACKGROUND_COLOR);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        JLabel titleLabel =
                new JLabel(
                        title,
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 26)
        );

        titleLabel.setForeground(TEXT_COLOR);

        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            LoginFrame loginFrame =
                    new LoginFrame();

            loginFrame.setVisible(true);
        }
    }

    // =========================================================
    // MAIN METHOD
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            DashboardFrame dashboard =
                    new DashboardFrame();

            dashboard.setVisible(true);
        });
    }
}
