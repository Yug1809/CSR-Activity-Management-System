
package com.csr.dao;

import com.csr.database.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    // =========================================
    // DASHBOARD STATISTICS MODEL
    // =========================================

    public static class DashboardStats {

        private int totalEvents;
        private int totalVolunteers;
        private int totalParticipants;
        private BigDecimal totalBudget;

        public DashboardStats(
                int totalEvents,
                int totalVolunteers,
                int totalParticipants,
                BigDecimal totalBudget
        ) {
            this.totalEvents = totalEvents;
            this.totalVolunteers = totalVolunteers;
            this.totalParticipants = totalParticipants;
            this.totalBudget = totalBudget;
        }

        public int getTotalEvents() {
            return totalEvents;
        }

        public int getTotalVolunteers() {
            return totalVolunteers;
        }

        public int getTotalParticipants() {
            return totalParticipants;
        }

        public BigDecimal getTotalBudget() {
            return totalBudget;
        }
    }

    // =========================================
    // RECENT EVENT MODEL
    // =========================================

    public static class RecentEvent {

        private int eventId;
        private String eventName;
        private Date eventDate;
        private String status;

        public RecentEvent(
                int eventId,
                String eventName,
                Date eventDate,
                String status
        ) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.status = status;
        }

        public int getEventId() {
            return eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public Date getEventDate() {
            return eventDate;
        }

        public String getStatus() {
            return status;
        }
    }

    // =========================================
    // GET DASHBOARD STATISTICS
    // =========================================

    public DashboardStats getDashboardStats()
            throws SQLException {

        String sql =
                "SELECT " +

                        "(SELECT COUNT(*) FROM EVENTS) AS total_events, " +

                        "(SELECT COUNT(*) FROM USERS " +
                        "WHERE role = 'Volunteer' AND is_active = 1) " +
                        "AS total_volunteers, " +

                        "(SELECT COUNT(*) FROM VOLUNTEERS) " +
                        "AS total_participants, " +

                        "(SELECT COALESCE(SUM(allocated_budget), 0) " +
                        "FROM BUDGET) AS total_budget";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return new DashboardStats(
                        resultSet.getInt("total_events"),
                        resultSet.getInt("total_volunteers"),
                        resultSet.getInt("total_participants"),
                        resultSet.getBigDecimal("total_budget")
                );
            }
        }

        return new DashboardStats(
                0, 0, 0, BigDecimal.ZERO
        );
    }

    // =========================================
    // GET RECENT EVENTS
    // =========================================

    public List<RecentEvent> getRecentEvents()
            throws SQLException {

        List<RecentEvent> events =
                new ArrayList<>();

        String sql =
                "SELECT event_id, event_name, `date`, status " +
                        "FROM EVENTS " +
                        "ORDER BY `date` DESC, event_id DESC " +
                        "LIMIT 5";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                RecentEvent event =
                        new RecentEvent(
                                resultSet.getInt("event_id"),
                                resultSet.getString("event_name"),
                                resultSet.getDate("date"),
                                resultSet.getString("status")
                        );

                events.add(event);
            }
        }

        return events;
    }
}
