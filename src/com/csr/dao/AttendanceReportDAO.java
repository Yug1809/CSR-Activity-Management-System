
package com.csr.dao;

import com.csr.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceReportDAO {

    // ==========================================
    // OVERALL ATTENDANCE SUMMARY
    // ==========================================

    public static class AttendanceSummary {

        private final int total;
        private final int present;
        private final int absent;
        private final int late;
        private final double percentage;

        public AttendanceSummary(
                int total,
                int present,
                int absent,
                int late,
                double percentage
        ) {
            this.total = total;
            this.present = present;
            this.absent = absent;
            this.late = late;
            this.percentage = percentage;
        }

        public int getTotal() {
            return total;
        }

        public int getPresent() {
            return present;
        }

        public int getAbsent() {
            return absent;
        }

        public int getLate() {
            return late;
        }

        public double getPercentage() {
            return percentage;
        }
    }

    // ==========================================
    // EVENT-WISE SUMMARY
    // ==========================================

    public static class EventSummary {

        private final int eventId;
        private final String eventName;
        private final int total;
        private final int present;
        private final int absent;
        private final int late;
        private final double percentage;

        public EventSummary(
                int eventId,
                String eventName,
                int total,
                int present,
                int absent,
                int late,
                double percentage
        ) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.total = total;
            this.present = present;
            this.absent = absent;
            this.late = late;
            this.percentage = percentage;
        }

        public int getEventId() {
            return eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public int getTotal() {
            return total;
        }

        public int getPresent() {
            return present;
        }

        public int getAbsent() {
            return absent;
        }

        public int getLate() {
            return late;
        }

        public double getPercentage() {
            return percentage;
        }
    }

    // ==========================================
    // 1. GET OVERALL ATTENDANCE SUMMARY
    // ==========================================

    public AttendanceSummary getOverallSummary()
            throws SQLException {

        String sql =
                "SELECT " +
                        "COUNT(*) AS total, " +
                        "SUM(CASE WHEN status = 'Present' " +
                        "THEN 1 ELSE 0 END) AS present_count, " +
                        "SUM(CASE WHEN status = 'Absent' " +
                        "THEN 1 ELSE 0 END) AS absent_count, " +
                        "SUM(CASE WHEN status = 'Late' " +
                        "THEN 1 ELSE 0 END) AS late_count " +
                        "FROM ATTENDANCE";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                int total = rs.getInt("total");
                int present = rs.getInt("present_count");
                int absent = rs.getInt("absent_count");
                int late = rs.getInt("late_count");

                double percentage = total == 0
                        ? 0
                        : ((present + late) * 100.0) / total;

                return new AttendanceSummary(
                        total,
                        present,
                        absent,
                        late,
                        percentage
                );
            }
        }

        return new AttendanceSummary(0, 0, 0, 0, 0);
    }

    // ==========================================
    // 2. GET EVENT-WISE ATTENDANCE SUMMARY
    // ==========================================

    public List<EventSummary> getEventSummaries()
            throws SQLException {

        List<EventSummary> summaries = new ArrayList<>();

        String sql =
                "SELECT " +
                        "e.event_id, " +
                        "e.event_name, " +
                        "COUNT(a.attendance_id) AS total, " +
                        "SUM(CASE WHEN a.status = 'Present' " +
                        "THEN 1 ELSE 0 END) AS present_count, " +
                        "SUM(CASE WHEN a.status = 'Absent' " +
                        "THEN 1 ELSE 0 END) AS absent_count, " +
                        "SUM(CASE WHEN a.status = 'Late' " +
                        "THEN 1 ELSE 0 END) AS late_count " +
                        "FROM EVENTS e " +
                        "LEFT JOIN ATTENDANCE a " +
                        "ON e.event_id = a.event_id " +
                        "GROUP BY e.event_id, e.event_name " +
                        "ORDER BY e.event_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int total = rs.getInt("total");
                int present = rs.getInt("present_count");
                int absent = rs.getInt("absent_count");
                int late = rs.getInt("late_count");

                double percentage = total == 0
                        ? 0
                        : ((present + late) * 100.0) / total;

                summaries.add(new EventSummary(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        total,
                        present,
                        absent,
                        late,
                        percentage
                ));
            }
        }

        return summaries;
    }
}
