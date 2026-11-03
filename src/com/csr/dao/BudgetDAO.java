
package com.csr.dao;

import com.csr.database.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    // =========================================
    // EVENT OPTION MODEL
    // =========================================

    public static class EventOption {

        private int eventId;
        private String eventName;

        public EventOption(int eventId, String eventName) {
            this.eventId = eventId;
            this.eventName = eventName;
        }

        public int getEventId() {
            return eventId;
        }

        public String getEventName() {
            return eventName;
        }

        @Override
        public String toString() {
            return eventName;
        }
    }

    // =========================================
    // BUDGET RECORD MODEL
    // =========================================

    public static class BudgetRecord {

        private int budgetId;
        private int eventId;
        private String eventName;
        private BigDecimal allocated;
        private BigDecimal spent;
        private BigDecimal remaining;

        public BudgetRecord(
                int budgetId,
                int eventId,
                String eventName,
                BigDecimal allocated,
                BigDecimal spent,
                BigDecimal remaining
        ) {
            this.budgetId = budgetId;
            this.eventId = eventId;
            this.eventName = eventName;
            this.allocated = allocated;
            this.spent = spent;
            this.remaining = remaining;
        }

        public int getBudgetId() {
            return budgetId;
        }

        public int getEventId() {
            return eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public BigDecimal getAllocated() {
            return allocated;
        }

        public BigDecimal getSpent() {
            return spent;
        }

        public BigDecimal getRemaining() {
            return remaining;
        }

        public double getUtilization() {

            if (allocated == null ||
                    allocated.compareTo(BigDecimal.ZERO) == 0) {
                return 0;
            }

            return spent
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            allocated,
                            2,
                            java.math.RoundingMode.HALF_UP
                    )
                    .doubleValue();
        }

        public String getStatus() {

            double utilization = getUtilization();

            if (utilization >= 90) {
                return "Near Limit";
            } else if (utilization <= 60) {
                return "Under Budget";
            }

            return "On Track";
        }
    }

    // =========================================
    // BUDGET SUMMARY MODEL
    // =========================================

    public static class BudgetSummary {

        private BigDecimal totalAllocated;
        private BigDecimal totalSpent;
        private BigDecimal totalRemaining;

        public BudgetSummary(
                BigDecimal totalAllocated,
                BigDecimal totalSpent,
                BigDecimal totalRemaining
        ) {
            this.totalAllocated = totalAllocated;
            this.totalSpent = totalSpent;
            this.totalRemaining = totalRemaining;
        }

        public BigDecimal getTotalAllocated() {
            return totalAllocated;
        }

        public BigDecimal getTotalSpent() {
            return totalSpent;
        }

        public BigDecimal getTotalRemaining() {
            return totalRemaining;
        }

        public double getUtilization() {

            if (totalAllocated.compareTo(BigDecimal.ZERO) == 0) {
                return 0;
            }

            return totalSpent
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            totalAllocated,
                            2,
                            java.math.RoundingMode.HALF_UP
                    )
                    .doubleValue();
        }
    }

    // =========================================
    // GET ALL EVENTS
    // =========================================

    public List<EventOption> getAllEvents()
            throws SQLException {

        List<EventOption> events = new ArrayList<>();

        String sql =
                "SELECT event_id, event_name " +
                        "FROM EVENTS " +
                        "ORDER BY event_name";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {

                events.add(
                        new EventOption(
                                rs.getInt("event_id"),
                                rs.getString("event_name")
                        )
                );
            }
        }

        return events;
    }

    // =========================================
    // GET ALL BUDGET RECORDS
    // =========================================

    public List<BudgetRecord> getAllBudgets()
            throws SQLException {

        List<BudgetRecord> budgets = new ArrayList<>();

        String sql =
                "SELECT b.budget_id, b.event_id, " +
                        "e.event_name, b.allocated_budget, " +
                        "b.spent_budget, b.remaining_budget " +
                        "FROM BUDGET b " +
                        "INNER JOIN EVENTS e ON b.event_id = e.event_id " +
                        "ORDER BY b.budget_id DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {

                budgets.add(
                        new BudgetRecord(
                                rs.getInt("budget_id"),
                                rs.getInt("event_id"),
                                rs.getString("event_name"),
                                rs.getBigDecimal("allocated_budget"),
                                rs.getBigDecimal("spent_budget"),
                                rs.getBigDecimal("remaining_budget")
                        )
                );
            }
        }

        return budgets;
    }

    // =========================================
    // GET BUDGET SUMMARY
    // =========================================

    public BudgetSummary getBudgetSummary()
            throws SQLException {

        String sql =
                "SELECT " +
                        "COALESCE(SUM(allocated_budget), 0) AS total_allocated, " +
                        "COALESCE(SUM(spent_budget), 0) AS total_spent, " +
                        "COALESCE(SUM(remaining_budget), 0) AS total_remaining " +
                        "FROM BUDGET";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs = statement.executeQuery()
        ) {

            if (rs.next()) {

                return new BudgetSummary(
                        rs.getBigDecimal("total_allocated"),
                        rs.getBigDecimal("total_spent"),
                        rs.getBigDecimal("total_remaining")
                );
            }
        }

        return new BudgetSummary(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    // =========================================
    // ADD NEW BUDGET
    // =========================================

    public boolean addBudget(
            int eventId,
            BigDecimal allocated,
            BigDecimal spent
    ) throws SQLException {

        validateAmounts(allocated, spent);

        String sql =
                "INSERT INTO BUDGET " +
                        "(event_id, allocated_budget, spent_budget) " +
                        "VALUES (?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, eventId);
            statement.setBigDecimal(2, allocated);
            statement.setBigDecimal(3, spent);

            return statement.executeUpdate() > 0;
        }
    }

    // =========================================
    // UPDATE EXISTING BUDGET
    // =========================================

    public boolean updateBudget(
            int budgetId,
            BigDecimal allocated,
            BigDecimal spent
    ) throws SQLException {

        validateAmounts(allocated, spent);

        String sql =
                "UPDATE BUDGET " +
                        "SET allocated_budget = ?, spent_budget = ? " +
                        "WHERE budget_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBigDecimal(1, allocated);
            statement.setBigDecimal(2, spent);
            statement.setInt(3, budgetId);

            return statement.executeUpdate() > 0;
        }
    }

    // =========================================
    // SEARCH BUDGETS
    // =========================================

    public List<BudgetRecord> searchBudgets(String searchText)
            throws SQLException {

        List<BudgetRecord> budgets = new ArrayList<>();

        String sql =
                "SELECT b.budget_id, b.event_id, " +
                        "e.event_name, b.allocated_budget, " +
                        "b.spent_budget, b.remaining_budget " +
                        "FROM BUDGET b " +
                        "INNER JOIN EVENTS e ON b.event_id = e.event_id " +
                        "WHERE e.event_name LIKE ? " +
                        "ORDER BY b.budget_id DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    "%" + searchText.trim() + "%"
            );

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    budgets.add(
                            new BudgetRecord(
                                    rs.getInt("budget_id"),
                                    rs.getInt("event_id"),
                                    rs.getString("event_name"),
                                    rs.getBigDecimal("allocated_budget"),
                                    rs.getBigDecimal("spent_budget"),
                                    rs.getBigDecimal("remaining_budget")
                            )
                    );
                }
            }
        }

        return budgets;
    }

    // =========================================
    // VALIDATE AMOUNTS
    // =========================================

    private void validateAmounts(
            BigDecimal allocated,
            BigDecimal spent
    ) {

        if (allocated == null || spent == null) {
            throw new IllegalArgumentException(
                    "Budget amounts cannot be empty."
            );
        }

        if (allocated.compareTo(BigDecimal.ZERO) < 0 ||
                spent.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Budget amounts cannot be negative."
            );
        }

        if (spent.compareTo(allocated) > 0) {

            throw new IllegalArgumentException(
                    "Spent amount cannot exceed allocated amount."
            );
        }
    }
}
