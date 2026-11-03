package com.csr.service;

import com.csr.dao.ReportDAO;
import com.csr.dao.EventDAO;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO;
    private final EventDAO eventDAO;

    public ReportService() {
        reportDAO = new ReportDAO();
        eventDAO = new EventDAO();
    }

    public ReportDAO.ReportData generateReport(
            String reportType, Integer eventId)
            throws SQLException {

        if (reportType == null || reportType.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a report type."
            );
        }

        return reportDAO.generateReport(reportType, eventId);
    }

    public List<EventDAO.EventRecord> getAllEvents()
            throws SQLException {

        return eventDAO.getAllEvents();
    }
}