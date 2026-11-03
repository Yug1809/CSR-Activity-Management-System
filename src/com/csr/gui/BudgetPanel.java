
package com.csr.gui;

import com.csr.dao.BudgetDAO;
import com.csr.dao.BudgetDAO.BudgetRecord;
import com.csr.dao.BudgetDAO.BudgetSummary;
import com.csr.dao.BudgetDAO.EventOption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class BudgetPanel extends JPanel {

    // ================= COLORS =================

    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color SECONDARY = new Color(100, 116, 139);
    private final Color BORDER = new Color(226, 232, 240);
    private final Color SUCCESS = new Color(22, 163, 74);

    private final BudgetDAO budgetDAO = new BudgetDAO();

    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private JLabel totalBudgetValue;
    private JLabel spentBudgetValue;
    private JLabel remainingBudgetValue;

    private final DecimalFormat moneyFormat =
            new DecimalFormat("#,##,##0.00");

    public BudgetPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ================= TOP SECTION =================

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Budget");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Manage and monitor budgets for CSR activities"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton addBudgetButton = createButton(
                "+  Add New Budget", PRIMARY, Color.WHITE
        );

        addBudgetButton.addActionListener(e -> showAddBudgetDialog());

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(addBudgetButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= CONTENT =================

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // ================= SUMMARY CARDS =================

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);

        summaryPanel.add(createSummaryCard(
                "Total Budget",
                "₹0.00",
                "Allocated across CSR activities",
                0
        ));

        summaryPanel.add(createSummaryCard(
                "Amount Spent",
                "₹0.00",
                "Total expenditure",
                1
        ));

        summaryPanel.add(createSummaryCard(
                "Remaining",
                "₹0.00",
                "Available for activities",
                2
        ));

        contentPanel.add(summaryPanel, BorderLayout.NORTH);

        // ================= LOWER CONTENT =================

        JPanel lowerPanel = new JPanel(new BorderLayout(0, 20));
        lowerPanel.setOpaque(false);
        lowerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // ================= SEARCH PANEL =================

        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setBackground(CARD);

        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel searchLabel = new JLabel("Search Budget");
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
                "Search", PRIMARY, Color.WHITE
        );

        JButton resetButton = createButton(
                "Reset", new Color(241, 245, 249), TEXT
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

        lowerPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE CARD =================

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createLineBorder(BORDER));

        String[] columns = {
                "ID", "Event", "Allocated", "Spent",
                "Remaining", "Utilization", "Status", "Actions"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        budgetTable = new JTable(tableModel);
        budgetTable.setRowHeight(48);
        budgetTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        budgetTable.setForeground(TEXT);
        budgetTable.setGridColor(BORDER);
        budgetTable.setSelectionBackground(new Color(239, 246, 255));
        budgetTable.setSelectionForeground(TEXT);
        budgetTable.setShowVerticalLines(false);

        budgetTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        budgetTable.getTableHeader().setForeground(TEXT);
        budgetTable.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );
        budgetTable.getTableHeader().setPreferredSize(
                new Dimension(0, 45)
        );

        int[] widths = {60, 220, 120, 120, 120, 110, 100, 120};

        for (int i = 0; i < widths.length; i++) {
            budgetTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        centerColumn(0);
        centerColumn(2);
        centerColumn(3);
        centerColumn(4);
        centerColumn(5);
        centerColumn(6);
        centerColumn(7);

        budgetTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new StatusRenderer());

        budgetTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(new ActionRenderer());

        // Double-click a row to edit its budget.
        budgetTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int viewRow = budgetTable.getSelectedRow();

                    if (viewRow != -1) {
                        int modelRow =
                                budgetTable.convertRowIndexToModel(viewRow);

                        showEditBudgetDialog(modelRow);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        lowerPanel.add(tableCard, BorderLayout.CENTER);

        contentPanel.add(lowerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================

        searchButton.addActionListener(e -> searchBudgets());

        searchField.addActionListener(e -> searchBudgets());

        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadBudgetData();
        });

        // Load actual database data.
        loadBudgetData();
    }

    // =====================================================
    // SUMMARY CARD
    // =====================================================

    private JPanel createSummaryCard(
            String title,
            String value,
            String description,
            int cardType
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(SECONDARY);

        if (cardType == 0) {
            totalBudgetValue = valueLabel;
        } else if (cardType == 1) {
            spentBudgetValue = valueLabel;
        } else {
            remainingBudgetValue = valueLabel;
        }

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);

        return card;
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(
                new EmptyBorder(10, 18, 10, 18)
        );

        return button;
    }

    // =====================================================
    // LOAD DATABASE DATA
    // =====================================================

    private void loadBudgetData() {

        try {

            tableModel.setRowCount(0);

            List<BudgetRecord> budgets =
                    budgetDAO.getAllBudgets();

            for (BudgetRecord budget : budgets) {
                addBudgetRow(budget);
            }

            loadBudgetSummary();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load budget records.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // LOAD SUMMARY
    // =====================================================

    private void loadBudgetSummary() {

        try {

            BudgetSummary summary =
                    budgetDAO.getBudgetSummary();

            totalBudgetValue.setText(
                    formatCurrency(summary.getTotalAllocated())
            );

            spentBudgetValue.setText(
                    formatCurrency(summary.getTotalSpent())
            );

            remainingBudgetValue.setText(
                    formatCurrency(summary.getTotalRemaining())
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            totalBudgetValue.setText("N/A");
            spentBudgetValue.setText("N/A");
            remainingBudgetValue.setText("N/A");
        }
    }

    // =====================================================
    // ADD TABLE ROW
    // =====================================================

    private void addBudgetRow(BudgetRecord budget) {

        tableModel.addRow(new Object[]{

                budget.getBudgetId(),

                budget.getEventName(),

                formatCurrency(budget.getAllocated()),

                formatCurrency(budget.getSpent()),

                formatCurrency(budget.getRemaining()),

                String.format("%.1f%%", budget.getUtilization()),

                budget.getStatus(),

                "View / Edit"
        });
    }

    // =====================================================
    // ADD BUDGET DIALOG
    // =====================================================

    private void showAddBudgetDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add New Budget",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        mainPanel.setLayout(
                new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Create New Budget");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(25));

        // Event dropdown populated from MySQL.

        mainPanel.add(createLabel("Event"));

        JComboBox<EventOption> eventBox = new JComboBox<>();

        try {

            List<EventOption> events = budgetDAO.getAllEvents();

            for (EventOption event : events) {
                eventBox.addItem(event);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load events from database.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        styleComboBox(eventBox);

        mainPanel.add(eventBox);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createLabel("Allocated Amount"));

        JTextField allocatedField = createTextField();
        mainPanel.add(allocatedField);

        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createLabel("Amount Spent"));

        JTextField spentField = createTextField();
        spentField.setText("0");

        mainPanel.add(spentField);
        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        buttonPanel.setOpaque(false);

        JButton cancelButton = createButton(
                "Cancel", new Color(241, 245, 249), TEXT
        );

        JButton saveButton = createButton(
                "Save Budget", PRIMARY, Color.WHITE
        );

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel);

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {

            EventOption selectedEvent =
                    (EventOption) eventBox.getSelectedItem();

            if (selectedEvent == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "No event is available. Create an event first.",
                        "No Event",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try {

                BigDecimal allocated =
                        new BigDecimal(allocatedField.getText().trim());

                BigDecimal spent =
                        new BigDecimal(spentField.getText().trim());

                validateAmounts(allocated, spent);

                budgetDAO.addBudget(
                        selectedEvent.getEventId(),
                        allocated,
                        spent
                );

                JOptionPane.showMessageDialog(
                        dialog,
                        "Budget added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();
                loadBudgetData();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter valid numeric amounts.",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                String message = ex.getMessage() == null
                        ? "Unable to save budget."
                        : ex.getMessage();

                if (message.toLowerCase().contains("duplicate")
                        || message.toLowerCase().contains("unique")) {

                    message =
                            "A budget already exists for this event. "
                                    + "Double-click its existing row to edit it.";
                }

                JOptionPane.showMessageDialog(
                        dialog,
                        message,
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // =====================================================
    // EDIT BUDGET DIALOG
    // =====================================================

    private void showEditBudgetDialog(int row) {

        int budgetId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        String eventName =
                tableModel.getValueAt(row, 1).toString();

        // Fetch the original numeric values from the database.
        BudgetRecord selectedBudget = null;

        try {

            for (BudgetRecord budget : budgetDAO.getAllBudgets()) {

                if (budget.getBudgetId() == budgetId) {
                    selectedBudget = budget;
                    break;
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load budget details.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (selectedBudget == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Budget record not found. Refresh the table.",
                    "Record Not Found",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Edit Budget",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(500, 430);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Edit Budget");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createLabel("Event"));

        JTextField eventField = createTextField();
        eventField.setText(eventName);
        eventField.setEditable(false);

        panel.add(eventField);
        panel.add(Box.createVerticalStrut(15));

        panel.add(createLabel("Allocated Amount"));

        JTextField allocatedField = createTextField();
        allocatedField.setText(
                selectedBudget.getAllocated().toPlainString()
        );

        panel.add(allocatedField);
        panel.add(Box.createVerticalStrut(15));

        panel.add(createLabel("Amount Spent"));

        JTextField spentField = createTextField();
        spentField.setText(
                selectedBudget.getSpent().toPlainString()
        );

        panel.add(spentField);
        panel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        buttonPanel.setOpaque(false);

        JButton cancelButton = createButton(
                "Cancel", new Color(241, 245, 249), TEXT
        );

        JButton updateButton = createButton(
                "Update Budget", PRIMARY, Color.WHITE
        );

        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);

        panel.add(buttonPanel);

        cancelButton.addActionListener(e -> dialog.dispose());

        updateButton.addActionListener(e -> {

            try {

                BigDecimal allocated =
                        new BigDecimal(allocatedField.getText().trim());

                BigDecimal spent =
                        new BigDecimal(spentField.getText().trim());

                validateAmounts(allocated, spent);

                budgetDAO.updateBudget(
                        budgetId,
                        allocated,
                        spent
                );

                JOptionPane.showMessageDialog(
                        dialog,
                        "Budget updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();
                loadBudgetData();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter valid numeric amounts.",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Unable to update budget.\n" + ex.getMessage(),
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // =====================================================
    // AMOUNT VALIDATION
    // =====================================================

    private void validateAmounts(
            BigDecimal allocated,
            BigDecimal spent
    ) {

        if (allocated.compareTo(BigDecimal.ZERO) < 0
                || spent.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Amounts cannot be negative."
            );
        }

        if (allocated.compareTo(BigDecimal.ZERO) == 0) {

            throw new IllegalArgumentException(
                    "Allocated amount must be greater than zero."
            );
        }

        if (spent.compareTo(allocated) > 0) {

            throw new IllegalArgumentException(
                    "Spent amount cannot exceed allocated amount."
            );
        }
    }

    // =====================================================
    // LABEL
    // =====================================================

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    // =====================================================
    // TEXT FIELD
    // =====================================================

    private JTextField createTextField() {

        JTextField field = new JTextField();

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40)
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
    // COMBO BOX
    // =====================================================

    private void styleComboBox(JComboBox<?> comboBox) {

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        comboBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40)
        );
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void searchBudgets() {

        String searchText = searchField.getText().trim();

        try {

            tableModel.setRowCount(0);

            List<BudgetRecord> budgets =
                    budgetDAO.searchBudgets(searchText);

            for (BudgetRecord budget : budgets) {
                addBudgetRow(budget);
            }

            if (budgets.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No budget records found.",
                        "Search Result",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to search budgets.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // CURRENCY FORMAT
    // =====================================================

    private String formatCurrency(BigDecimal amount) {

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        return "₹" + moneyFormat.format(amount);
    }

    // =====================================================
    // CENTER COLUMN
    // =====================================================

    private void centerColumn(int column) {

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        budgetTable.getColumnModel()
                .getColumn(column)
                .setCellRenderer(renderer);
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
                int column
        ) {

            JLabel label = new JLabel(
                    value == null ? "" : value.toString()
            );

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            String status = value == null ? "" : value.toString();

            if (status.equals("On Track")) {

                label.setForeground(SUCCESS);
                label.setBackground(new Color(220, 252, 231));

            } else if (status.equals("Under Budget")) {

                label.setForeground(PRIMARY);
                label.setBackground(new Color(219, 234, 254));

            } else {

                label.setForeground(new Color(180, 83, 9));
                label.setBackground(new Color(254, 243, 199));
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
                int column
        ) {

            JLabel label = new JLabel(
                    value == null ? "" : value.toString()
            );

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(PRIMARY);

            return label;
        }
    }
}
