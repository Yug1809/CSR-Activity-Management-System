
package com.csr.dao;

import com.csr.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    // ================= VOLUNTEER OPTION =================

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

    // ================= EVENT OPTION =================

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

    // ================= FEEDBACK RECORD =================

    public static class FeedbackRecord {

        private final int feedbackId;
        private final int volunteerId;
        private final int eventId;
        private final String volunteerName;
        private final String eventName;
        private final int rating;
        private final String comments;

        public FeedbackRecord(
                int feedbackId,
                int volunteerId,
                int eventId,
                String volunteerName,
                String eventName,
                int rating,
                String comments
        ) {
            this.feedbackId = feedbackId;
            this.volunteerId = volunteerId;
            this.eventId = eventId;
            this.volunteerName = volunteerName;
            this.eventName = eventName;
            this.rating = rating;
            this.comments = comments;
        }

        public int getFeedbackId() {
            return feedbackId;
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

        public int getRating() {
            return rating;
        }

        public String getComments() {
            return comments;
        }
    }

    // ================= GET VOLUNTEERS =================

    public List<VolunteerOption> getAllVolunteers()
            throws SQLException {

        List<VolunteerOption> volunteers = new ArrayList<>();

        String sql =
                "SELECT v.volunteer_id, u.name " +
                        "FROM VOLUNTEERS v " +
                        "JOIN USERS u ON v.user_id = u.id " +
                        "WHERE u.is_active = 1 " +
                        "ORDER BY u.name";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {

                volunteers.add(
                        new VolunteerOption(
                                rs.getInt("volunteer_id"),
                                rs.getString("name")
                        )
                );
            }
        }

        return volunteers;
    }

    // ================= GET EVENTS =================

    public List<EventOption> getAllEvents()
            throws SQLException {

        List<EventOption> events = new ArrayList<>();

        String sql =
                "SELECT event_id, event_name " +
                        "FROM EVENTS " +
                        "ORDER BY event_name";

        try (
                Connection connection = DBConnection.getConnection();
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

    // ================= ADD FEEDBACK =================

    public void addFeedback(
            int volunteerId,
            int eventId,
            int rating,
            String comments
    ) throws SQLException {

        if (volunteerId <= 0 || eventId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid volunteer and event."
            );
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5."
            );
        }

        if (comments == null || comments.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Feedback comments cannot be empty."
            );
        }

        String sql =
                "INSERT INTO FEEDBACK " +
                        "(volunteer_id, event_id, rating, comments) " +
                        "VALUES (?, ?, ?, ?)";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, volunteerId);
            statement.setInt(2, eventId);
            statement.setInt(3, rating);
            statement.setString(4, comments.trim());

            statement.executeUpdate();
        }
    }

    // ================= GET ALL FEEDBACK =================

    public List<FeedbackRecord> getAllFeedback()
            throws SQLException {

        List<FeedbackRecord> feedbackList = new ArrayList<>();

        String sql =
                "SELECT f.feedback_id, f.volunteer_id, f.event_id, " +
                        "u.name AS volunteer_name, " +
                        "e.event_name, f.rating, f.comments " +
                        "FROM FEEDBACK f " +
                        "JOIN VOLUNTEERS v ON f.volunteer_id = v.volunteer_id " +
                        "JOIN USERS u ON v.user_id = u.id " +
                        "JOIN EVENTS e ON f.event_id = e.event_id " +
                        "ORDER BY f.feedback_id DESC";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {

                feedbackList.add(
                        new FeedbackRecord(
                                rs.getInt("feedback_id"),
                                rs.getInt("volunteer_id"),
                                rs.getInt("event_id"),
                                rs.getString("volunteer_name"),
                                rs.getString("event_name"),
                                rs.getInt("rating"),
                                rs.getString("comments")
                        )
                );
            }
        }

        return feedbackList;
    }

    // ================= SEARCH FEEDBACK =================

    public List<FeedbackRecord> searchFeedback(String keyword)
            throws SQLException {

        List<FeedbackRecord> feedbackList = new ArrayList<>();

        String sql =
                "SELECT f.feedback_id, f.volunteer_id, f.event_id, " +
                        "u.name AS volunteer_name, " +
                        "e.event_name, f.rating, f.comments " +
                        "FROM FEEDBACK f " +
                        "JOIN VOLUNTEERS v ON f.volunteer_id = v.volunteer_id " +
                        "JOIN USERS u ON v.user_id = u.id " +
                        "JOIN EVENTS e ON f.event_id = e.event_id " +
                        "WHERE CAST(f.feedback_id AS CHAR) LIKE ? " +
                        "OR u.name LIKE ? " +
                        "OR e.event_name LIKE ? " +
                        "OR f.comments LIKE ? " +
                        "OR CAST(f.rating AS CHAR) LIKE ? " +
                        "ORDER BY f.feedback_id DESC";

        String searchValue = "%" +
                (keyword == null ? "" : keyword.trim()) +
                "%";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            for (int i = 1; i <= 5; i++) {
                statement.setString(i, searchValue);
            }

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    feedbackList.add(
                            new FeedbackRecord(
                                    rs.getInt("feedback_id"),
                                    rs.getInt("volunteer_id"),
                                    rs.getInt("event_id"),
                                    rs.getString("volunteer_name"),
                                    rs.getString("event_name"),
                                    rs.getInt("rating"),
                                    rs.getString("comments")
                            )
                    );
                }
            }
        }

        return feedbackList;
    }
}
