
package com.csr.service;

import com.csr.dao.AttendanceReportDAO;

import java.sql.SQLException;
import java.util.List;

public class AttendanceReportService {

    private final AttendanceReportDAO reportDAO;

    public AttendanceReportService() {
        this.reportDAO = new AttendanceReportDAO();
    }

    // ==========================================
    // GET OVERALL ATTENDANCE SUMMARY
    // ==========================================

    public AttendanceReportDAO.AttendanceSummary getOverallSummary()
            throws SQLException {

        return reportDAO.getOverallSummary();
    }

    // ==========================================
    // GET EVENT-WISE ATTENDANCE REPORTS
    // ==========================================

    public List<AttendanceReportDAO.EventSummary> getEventSummaries()
            throws SQLException {

        return reportDAO.getEventSummaries();
    }
}
