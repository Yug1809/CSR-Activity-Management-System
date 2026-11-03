
package com.csr.dao;

import com.csr.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // ==========================================
    // EVENT OPTION
    // ==========================================

    public static class EventOption {
        private final int eventId;
        private final String eventName;

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

    // ==========================================
    // VOLUNTEER OPTION
    // ==========================================

    public static class VolunteerOption {
        private final int volunteerId;
        private final String volunteerName;

        public VolunteerOption(int volunteerId, String volunteerName) {
            this.volunteerId = volunteerId;
            this.volunteerName = volunteerName;
        }

        public int getVolunteerId() {
            return volunteerId;
        }

        public String getVolunteerName() {
            return volunteerName;
        }

        @Override
        public String toString() {
            return volunteerName;
        }
    }

    // ==========================================
    // ATTENDANCE RECORD
    // ==========================================

    public static class AttendanceRecord {
        private final int attendanceId;
        private final int volunteerId;
        private final int eventId;
        private final String volunteerName;
        private final String eventName;
        private final Date attendanceDate;
        private final String status;

        public AttendanceRecord(
                int attendanceId,
                int volunteerId,
                int eventId,
                String volunteerName,
                String eventName,
                Date attendanceDate,
                String status
        ) {
            this.attendanceId = attendanceId;
            this.volunteerId = volunteerId;
            this.eventId = eventId;
            this.volunteerName = volunteerName;
            this.eventName = eventName;
            this.attendanceDate = attendanceDate;
            this.status = status;
        }

        public int getAttendanceId() {
            return attendanceId;
        }

        public int getVolunteerId() {
            return volunteerId;
        }

        public int getEventId() {
            return eventId;
        }

        public String getVolunteerName() {
            return volunteerName;
        }

        public String getEventName() {
            return eventName;
        }

        public Date getAttendanceDate() {
            return attendanceDate;
        }

        public String getStatus() {
            return status;
        }
    }

    // ==========================================
    // 1. GET ALL EVENTS
    // ==========================================

    public List<EventOption> getAllEvents() throws SQLException {

        List<EventOption> events = new ArrayList<>();

        String sql =
                "SELECT event_id, event_name " +
                        "FROM EVENTS " +
                        "ORDER BY event_name";

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

    // ==========================================
    // 2. GET VOLUNTEERS REGISTERED FOR AN EVENT
    // ==========================================

    public List<VolunteerOption> getVolunteersByEvent(
            int eventId
    ) throws SQLException {

        List<VolunteerOption> volunteers = new ArrayList<>();

        String sql =
                "SELECT v.volunteer_id, u.name " +
                        "FROM VOLUNTEERS v " +
                        "INNER JOIN USERS u ON v.user_id = u.id " +
                        "WHERE v.event_id = ? " +
                        "AND u.is_active = 1 " +
                        "ORDER BY u.name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    volunteers.add(new VolunteerOption(
                            rs.getInt("volunteer_id"),
                            rs.getString("name")
                    ));
                }
            }
        }

        return volunteers;
    }

    // ==========================================
    // 3. MARK ATTENDANCE
    // ==========================================

    public boolean markAttendance(
            int volunteerId,
            int eventId,
            String status,
            Date attendanceDate
    ) throws SQLException {

        if (!status.equals("Present")
                && !status.equals("Absent")
                && !status.equals("Late")) {

            throw new IllegalArgumentException(
                    "Invalid attendance status."
            );
        }

        if (attendanceDate == null) {
            throw new IllegalArgumentException(
                    "Attendance date cannot be empty."
            );
        }

        String sql =
                "INSERT INTO ATTENDANCE " +
                        "(volunteer_id, event_id, status, attendance_date) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ps.setInt(2, eventId);
            ps.setString(3, status);
            ps.setDate(4, attendanceDate);

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException ex) {

            throw new SQLException(
                    "Attendance already exists for this volunteer " +
                            "on this date, or the selected registration is invalid.",
                    ex
            );
        }
    }

    // ==========================================
    // 4. GET ALL ATTENDANCE RECORDS
    // ==========================================

    public List<AttendanceRecord> getAllAttendance()
            throws SQLException {

        List<AttendanceRecord> records = new ArrayList<>();

        String sql =
                "SELECT a.attendance_id, " +
                        "a.volunteer_id, " +
                        "a.event_id, " +
                        "u.name AS volunteer_name, " +
                        "e.event_name, " +
                        "a.attendance_date, " +
                        "a.status " +
                        "FROM ATTENDANCE a " +
                        "INNER JOIN VOLUNTEERS v " +
                        "ON a.volunteer_id = v.volunteer_id " +
                        "INNER JOIN USERS u ON v.user_id = u.id " +
                        "INNER JOIN EVENTS e ON a.event_id = e.event_id " +
                        "ORDER BY a.attendance_date DESC, a.attendance_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                records.add(new AttendanceRecord(
                        rs.getInt("attendance_id"),
                        rs.getInt("volunteer_id"),
                        rs.getInt("event_id"),
                        rs.getString("volunteer_name"),
                        rs.getString("event_name"),
                        rs.getDate("attendance_date"),
                        rs.getString("status")
                ));
            }
        }

        return records;
    }

    // ==========================================
    // 5. SEARCH ATTENDANCE
    // ==========================================

    public List<AttendanceRecord> searchAttendance(
            String searchText
    ) throws SQLException {

        List<AttendanceRecord> records = new ArrayList<>();

        String sql =
                "SELECT a.attendance_id, " +
                        "a.volunteer_id, " +
                        "a.event_id, " +
                        "u.name AS volunteer_name, " +
                        "e.event_name, " +
                        "a.attendance_date, " +
                        "a.status " +
                        "FROM ATTENDANCE a " +
                        "INNER JOIN VOLUNTEERS v " +
                        "ON a.volunteer_id = v.volunteer_id " +
                        "INNER JOIN USERS u ON v.user_id = u.id " +
                        "INNER JOIN EVENTS e ON a.event_id = e.event_id " +
                        "WHERE LOWER(u.name) LIKE ? " +
                        "OR LOWER(e.event_name) LIKE ? " +
                        "OR LOWER(a.status) LIKE ? " +
                        "ORDER BY a.attendance_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String search = "%" + searchText.toLowerCase().trim() + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    records.add(new AttendanceRecord(
                            rs.getInt("attendance_id"),
                            rs.getInt("volunteer_id"),
                            rs.getInt("event_id"),
                            rs.getString("volunteer_name"),
                            rs.getString("event_name"),
                            rs.getDate("attendance_date"),
                            rs.getString("status")
                    ));
                }
            }
        }

        return records;
    }
}
