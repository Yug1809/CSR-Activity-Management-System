
package com.csr.dao;

import com.csr.database.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public static class EventRecord {
        public int id;
        public String name;
        public Date date;
        public String location;
        public int volunteerCount;
        public BigDecimal budget;
        public String status;

        public EventRecord(
                int id, String name, Date date, String location,
                int volunteerCount, BigDecimal budget, String status) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.location = location;
            this.volunteerCount = volunteerCount;
            this.budget = budget;
            this.status = status;
        }
    }

    public List<EventRecord> getAllEvents() throws SQLException {
        String sql = """
                SELECT e.event_id, e.event_name, e.`date`,
                       e.location, e.budget, e.status,
                       COUNT(DISTINCT v.user_id) AS volunteer_count
                FROM EVENTS e
                LEFT JOIN VOLUNTEERS v ON v.event_id = e.event_id
                GROUP BY e.event_id, e.event_name, e.`date`,
                         e.location, e.budget, e.status
                ORDER BY e.`date` DESC
                """;

        List<EventRecord> events = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                events.add(new EventRecord(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getDate("date"),
                        rs.getString("location"),
                        rs.getInt("volunteer_count"),
                        rs.getBigDecimal("budget"),
                        rs.getString("status")
                ));
            }
        }

        return events;
    }

    public List<EventRecord> searchEvents(String search)
            throws SQLException {

        String sql = """
                SELECT e.event_id, e.event_name, e.`date`,
                       e.location, e.budget, e.status,
                       COUNT(DISTINCT v.user_id) AS volunteer_count
                FROM EVENTS e
                LEFT JOIN VOLUNTEERS v ON v.event_id = e.event_id
                WHERE e.event_name LIKE ?
                   OR e.location LIKE ?
                   OR e.status LIKE ?
                GROUP BY e.event_id, e.event_name, e.`date`,
                         e.location, e.budget, e.status
                ORDER BY e.`date` DESC
                """;

        List<EventRecord> events = new ArrayList<>();
        String pattern = "%" + search.trim() + "%";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(new EventRecord(
                            rs.getInt("event_id"),
                            rs.getString("event_name"),
                            rs.getDate("date"),
                            rs.getString("location"),
                            rs.getInt("volunteer_count"),
                            rs.getBigDecimal("budget"),
                            rs.getString("status")
                    ));
                }
            }
        }

        return events;
    }

    public int addEvent(
            String name, String description, String category,
            String location, Date date, BigDecimal budget)
            throws SQLException {

        String sql = """
                INSERT INTO EVENTS
                    (event_name, description, category, location,
                     `date`, budget, status)
                VALUES (?, ?, ?, ?, ?, ?, 'Upcoming')
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, category);
            ps.setString(4, location);
            ps.setDate(5, date);
            ps.setBigDecimal(6, budget);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Event was saved, but its ID was not returned.");
    }
}
