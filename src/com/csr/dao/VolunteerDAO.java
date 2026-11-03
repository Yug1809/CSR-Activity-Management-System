package com.csr.dao;

import com.csr.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolunteerDAO {

    public static class EventOption {
        public final int id;
        public final String name;

        public EventOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (ID: " + id + ")";
        }
    }

    public static class VolunteerRecord {
        public final int volunteerId;
        public final String name;
        public final String email;
        public final String phone;
        public final int eventCount;
        public final String attendance;
        public final String status;

        public VolunteerRecord(int volunteerId, String name, String email,
                               String phone, int eventCount,
                               String attendance, String status) {
            this.volunteerId = volunteerId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.eventCount = eventCount;
            this.attendance = attendance;
            this.status = status;
        }
    }

    public List<EventOption> getAllEvents() throws SQLException {
        String sql = "SELECT event_id, event_name FROM EVENTS ORDER BY event_name";
        List<EventOption> events = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                events.add(new EventOption(
                        rs.getInt("event_id"),
                        rs.getString("event_name")
                ));
            }
        }
        return events;
    }

    /** Creates a Volunteer user and links that user to the selected event atomically. */
    public void addVolunteer(String name, String email, String phone,
                             String passwordHash, int eventId) throws SQLException {
        String findUserSql = "SELECT id FROM USERS WHERE email = ? OR username = ?";
        String insertUserSql = "INSERT INTO USERS (name, email, password, role, phone, username) " +
                "VALUES (?, ?, ?, 'Volunteer', ?, ?)";
        String insertVolunteerSql = "INSERT INTO VOLUNTEERS " +
                "(user_id, event_id, registration_date, hours_worked) VALUES (?, ?, CURRENT_DATE, 0)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement find = con.prepareStatement(findUserSql)) {
                    find.setString(1, email);
                    find.setString(2, email);
                    try (ResultSet rs = find.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("A user with this email/username already exists.");
                        }
                    }
                }

                try (PreparedStatement insertUser = con.prepareStatement(
                        insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertUser.setString(1, name);
                    insertUser.setString(2, email);
                    insertUser.setString(3, passwordHash);
                    insertUser.setString(4, phone);
                    // Email is also used as the login username.
                    insertUser.setString(5, email);
                    insertUser.executeUpdate();

                    try (ResultSet keys = insertUser.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not retrieve the newly created user ID.");
                        }
                        userId = keys.getInt(1);
                    }
                }

                try (PreparedStatement insertVolunteer = con.prepareStatement(insertVolunteerSql)) {
                    insertVolunteer.setInt(1, userId);
                    insertVolunteer.setInt(2, eventId);
                    insertVolunteer.executeUpdate();
                }

                con.commit();
            } catch (SQLException | RuntimeException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                throw ex;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection is closed by try-with-resources.
                }
            }
        }
    }

    // ================= UPDATE VOLUNTEER DETAILS =================
    public void updateVolunteer(int userId, String name, String email, String phone)
            throws SQLException {
        String duplicateSql =
                "SELECT id FROM USERS WHERE (email = ? OR username = ?) AND id <> ?";
        String updateSql =
                "UPDATE USERS SET name = ?, email = ?, username = ?, phone = ? " +
                        "WHERE id = ? AND role = 'Volunteer'";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement check = con.prepareStatement(duplicateSql)) {
                check.setString(1, email);
                check.setString(2, email);
                check.setInt(3, userId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("Another user already uses this email/username.");
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setInt(5, userId);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Volunteer was not found or could not be updated.");
                }
            }
        }
    }

    // ================= DEACTIVATE VOLUNTEER =================
    // Soft deactivation preserves event registrations and attendance history.
    public void deactivateVolunteer(int userId) throws SQLException {
        String sql = "UPDATE USERS SET is_active = 0 WHERE id = ? AND role = 'Volunteer'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Volunteer was not found or is already inactive.");
            }
        }
    }

    public List<VolunteerRecord> getAllVolunteers() throws SQLException {
        return searchVolunteers("");
    }

    // ================= EVENT-WISE VOLUNTEERS =================
    public List<VolunteerRecord> getVolunteersByEvent(
            int eventId, String searchText) throws SQLException {

        String sql =
                "SELECT u.id, u.name, u.email, " +
                        "COALESCE(u.phone, '') AS phone, " +
                        "u.is_active, " +
                        "COUNT(DISTINCT v.event_id) AS event_count, " +
                        "CASE WHEN COUNT(a.attendance_id) = 0 THEN 0 " +
                        "ELSE ROUND(100.0 * " +
                        "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) / " +
                        "COUNT(a.attendance_id)) END AS attendance_pct " +
                        "FROM USERS u " +
                        "JOIN VOLUNTEERS v ON v.user_id = u.id " +
                        "LEFT JOIN ATTENDANCE a ON a.volunteer_id = v.volunteer_id " +
                        "WHERE u.role = 'Volunteer' " +
                        "AND v.event_id = ? " +
                        "AND (u.name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?) " +
                        "GROUP BY u.id, u.name, u.email, u.phone, u.is_active " +
                        "ORDER BY u.id DESC";

        String pattern = "%" +
                (searchText == null ? "" : searchText.trim()) + "%";

        List<VolunteerRecord> volunteers = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    volunteers.add(new VolunteerRecord(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getInt("event_count"),
                            rs.getInt("attendance_pct") + "%",
                            rs.getInt("is_active") == 1
                                    ? "Active" : "Inactive"
                    ));
                }
            }
        }

        return volunteers;
    }


    public List<VolunteerRecord> searchVolunteers(String searchText) throws SQLException {
        String sql = "SELECT u.id, u.name, u.email, COALESCE(u.phone, '') AS phone, " +
                "u.is_active, COUNT(DISTINCT v.event_id) AS event_count, " +
                "CASE WHEN COUNT(a.attendance_id) = 0 THEN 0 " +
                "ELSE ROUND(100.0 * SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) " +
                "/ COUNT(a.attendance_id)) END AS attendance_pct " +
                "FROM USERS u " +
                "JOIN VOLUNTEERS v ON v.user_id = u.id " +
                "LEFT JOIN ATTENDANCE a ON a.volunteer_id = v.volunteer_id " +
                "WHERE u.role = 'Volunteer' " +
                "AND (u.name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?) " +
                "GROUP BY u.id, u.name, u.email, u.phone ORDER BY u.id DESC";

        String pattern = "%" + (searchText == null ? "" : searchText.trim()) + "%";
        List<VolunteerRecord> volunteers = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    volunteers.add(new VolunteerRecord(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getInt("event_count"),
                            rs.getInt("attendance_pct") + "%",
                            rs.getInt("is_active") == 1 ? "Active" : "Inactive"
                    ));
                }
            }
        }
        return volunteers;
    }
}
