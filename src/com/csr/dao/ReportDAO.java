package com.csr.dao;

import com.csr.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public static class ReportData {
        private final String[] columns;
        private final List<String[]> rows;

        public ReportData(String[] columns, List<String[]> rows) {
            this.columns = columns;
            this.rows = rows;
        }

        public String[] getColumns() {
            return columns;
        }

        public List<String[]> getRows() {
            return rows;
        }
    }

    public ReportData generateReport(String reportType, Integer eventId)
            throws SQLException {

        String sql;

        switch (reportType) {

            case "Event Report":
                sql = """
                    SELECT event_id, event_name, category, location,
                           `date`, budget, status
                    FROM EVENTS e
                    """ + eventFilter(eventId) +
                        " ORDER BY `date` DESC";
                break;

            case "Volunteer Report":
                sql = """
                    SELECT u.id, u.name, u.email, u.phone,
                           e.event_name, v.registration_date,
                           v.hours_worked
                    FROM VOLUNTEERS v
                    JOIN USERS u ON v.user_id = u.id
                    JOIN EVENTS e ON v.event_id = e.event_id
                    """ + eventFilter(eventId) +
                        " ORDER BY u.name";
                break;

            case "Attendance Report":
                sql = """
                    SELECT a.attendance_id, u.name, e.event_name,
                           a.attendance_date, a.status
                    FROM ATTENDANCE a
                    JOIN VOLUNTEERS v ON a.volunteer_id = v.volunteer_id
                    JOIN USERS u ON v.user_id = u.id
                    JOIN EVENTS e ON a.event_id = e.event_id
                    """ + eventFilter(eventId) +
                        " ORDER BY a.attendance_date DESC";
                break;

            case "Budget Report":
                sql = """
                    SELECT e.event_id, e.event_name,
                           b.allocated_budget, b.spent_budget,
                           b.remaining_budget
                    FROM BUDGET b
                    JOIN EVENTS e ON b.event_id = e.event_id
                    """ + eventFilter(eventId) +
                        " ORDER BY e.event_name";
                break;

            case "Feedback Report":
                sql = """
                    SELECT f.feedback_id, u.name, e.event_name,
                           f.rating, f.comments
                    FROM FEEDBACK f
                    JOIN VOLUNTEERS v ON f.volunteer_id = v.volunteer_id
                    JOIN USERS u ON v.user_id = u.id
                    JOIN EVENTS e ON f.event_id = e.event_id
                    """ + eventFilter(eventId) +
                        " ORDER BY f.feedback_id DESC";
                break;

            case "Complete CSR Report":
                sql = """
                    SELECT
                        e.event_id,
                        e.event_name,
                        e.category,
                        e.location,
                        e.`date`,
                        e.status,
                        COUNT(DISTINCT v.volunteer_id) AS volunteers,
                        COUNT(DISTINCT a.attendance_id) AS attendance_records,
                        SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END)
                            AS present_count,
                        SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END)
                            AS absent_count,
                        COALESCE(b.allocated_budget, 0) AS allocated_budget,
                        COALESCE(b.spent_budget, 0) AS spent_budget,
                        COALESCE(b.remaining_budget, 0) AS remaining_budget,
                        COUNT(DISTINCT f.feedback_id) AS feedback_count,
                        COALESCE(AVG(f.rating), 0) AS average_rating
                    FROM EVENTS e
                    LEFT JOIN VOLUNTEERS v ON e.event_id = v.event_id
                    LEFT JOIN ATTENDANCE a ON
                        e.event_id = a.event_id
                        AND v.volunteer_id = a.volunteer_id
                    LEFT JOIN BUDGET b ON e.event_id = b.event_id
                    LEFT JOIN FEEDBACK f ON
                        e.event_id = f.event_id
                        AND v.volunteer_id = f.volunteer_id
                    """ + eventFilter(eventId) +
                        """
                        GROUP BY e.event_id, e.event_name, e.category,
                                 e.location, e.`date`, e.status,
                                 b.allocated_budget, b.spent_budget,
                                 b.remaining_budget
                        ORDER BY e.`date` DESC
                        """;
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid report type: " + reportType
                );
        }

        return executeReport(sql, eventId);
    }

    private String eventFilter(Integer eventId) {
        return eventId == null ? "" : " WHERE e.event_id = ? ";
    }

    private ReportData executeReport(String sql, Integer eventId)
            throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (eventId != null) {
                statement.setInt(1, eventId);
            }

            try (ResultSet rs = statement.executeQuery()) {

                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();

                String[] columns = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    columns[i] = metadata.getColumnLabel(i + 1);
                }

                List<String[]> rows = new ArrayList<>();

                while (rs.next()) {
                    String[] row = new String[columnCount];

                    for (int i = 0; i < columnCount; i++) {
                        Object value = rs.getObject(i + 1);
                        row[i] = value == null ? "" : value.toString();
                    }

                    rows.add(row);
                }

                return new ReportData(columns, rows);
            }
        }
    }
}