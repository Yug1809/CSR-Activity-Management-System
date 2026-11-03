
package com.csr.service;

import com.csr.dao.AttendanceDAO;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    // Get all events
    public List<AttendanceDAO.EventOption> getAllEvents()
            throws SQLException {

        return attendanceDAO.getAllEvents();
    }

    // Get volunteers registered for an event
    public List<AttendanceDAO.VolunteerOption> getVolunteersByEvent(
            int eventId
    ) throws SQLException {

        if (eventId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid event."
            );
        }

        return attendanceDAO.getVolunteersByEvent(eventId);
    }

    // Save attendance
    public boolean markAttendance(
            int volunteerId,
            int eventId,
            String status,
            Date attendanceDate
    ) throws SQLException {

        if (volunteerId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a volunteer."
            );
        }

        if (eventId <= 0) {
            throw new IllegalArgumentException(
                    "Please select an event."
            );
        }

        if (status == null ||
                !(status.equals("Present")
                        || status.equals("Absent")
                        || status.equals("Late"))) {

            throw new IllegalArgumentException(
                    "Please select a valid attendance status."
            );
        }

        if (attendanceDate == null) {
            throw new IllegalArgumentException(
                    "Please select an attendance date."
            );
        }

        return attendanceDAO.markAttendance(
                volunteerId,
                eventId,
                status,
                attendanceDate
        );
    }

    // Get all attendance records
    public List<AttendanceDAO.AttendanceRecord> getAllAttendance()
            throws SQLException {

        return attendanceDAO.getAllAttendance();
    }

    // Search attendance records
    public List<AttendanceDAO.AttendanceRecord> searchAttendance(
            String searchText
    ) throws SQLException {

        if (searchText == null || searchText.trim().isEmpty()) {
            return attendanceDAO.getAllAttendance();
        }

        return attendanceDAO.searchAttendance(searchText);
    }
}
