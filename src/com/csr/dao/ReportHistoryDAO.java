
package com.csr.dao;

import com.csr.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportHistoryDAO {

    public static class ReportHistoryRecord {

        private final int reportId;
        private final String reportName;
        private final String reportType;
        private final String eventName;
        private final String fileFormat;
        private final int recordCount;
        private final Timestamp generatedAt;
        private final String filePath;

        public ReportHistoryRecord(
                int reportId,
                String reportName,
                String reportType,
                String eventName,
                String fileFormat,
                int recordCount,
                Timestamp generatedAt,
                String filePath) {

            this.reportId = reportId;
            this.reportName = reportName;
            this.reportType = reportType;
            this.eventName = eventName;
            this.fileFormat = fileFormat;
            this.recordCount = recordCount;
            this.generatedAt = generatedAt;
            this.filePath = filePath;
        }

        public int getReportId() {
            return reportId;
        }

        public String getReportName() {
            return reportName;
        }

        public String getReportType() {
            return reportType;
        }

        public String getEventName() {
            return eventName;
        }

        public String getFileFormat() {
            return fileFormat;
        }

        public int getRecordCount() {
            return recordCount;
        }

        public Timestamp getGeneratedAt() {
            return generatedAt;
        }

        public String getFilePath() {
            return filePath;
        }
    }

    // Save generated report history
    public int saveReport(
            String reportName,
            String reportType,
            Integer eventId,
            String fileFormat,
            Integer generatedBy,
            int recordCount,
            String filePath) throws SQLException {

        String sql = """
                INSERT INTO REPORT_HISTORY
                (report_name, report_type, event_id,
                 file_format, generated_by, record_count, file_path)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, reportName);
            ps.setString(2, reportType);

            if (eventId == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, eventId);
            }

            ps.setString(4, fileFormat);

            if (generatedBy == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, generatedBy);
            }

            ps.setInt(6, recordCount);
            ps.setString(7, filePath);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Report saved, but report ID was not returned."
        );
    }

    // Retrieve all generated reports
    public List<ReportHistoryRecord> getAllReports()
            throws SQLException {

        String sql = """
                SELECT
                    r.report_id,
                    r.report_name,
                    r.report_type,
                    COALESCE(e.event_name, 'All Events') AS event_name,
                    r.file_format,
                    r.record_count,
                    r.generated_at,
                    r.file_path
                FROM REPORT_HISTORY r
                LEFT JOIN EVENTS e ON r.event_id = e.event_id
                ORDER BY r.generated_at DESC, r.report_id DESC
                """;

        List<ReportHistoryRecord> reports = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                reports.add(new ReportHistoryRecord(
                        rs.getInt("report_id"),
                        rs.getString("report_name"),
                        rs.getString("report_type"),
                        rs.getString("event_name"),
                        rs.getString("file_format"),
                        rs.getInt("record_count"),
                        rs.getTimestamp("generated_at"),
                        rs.getString("file_path")
                ));
            }
        }

        return reports;
    }

    // Search report history
    public List<ReportHistoryRecord> searchReports(String keyword)
            throws SQLException {

        String sql = """
                SELECT
                    r.report_id,
                    r.report_name,
                    r.report_type,
                    COALESCE(e.event_name, 'All Events') AS event_name,
                    r.file_format,
                    r.record_count,
                    r.generated_at,
                    r.file_path
                FROM REPORT_HISTORY r
                LEFT JOIN EVENTS e ON r.event_id = e.event_id
                WHERE r.report_name LIKE ?
                   OR r.report_type LIKE ?
                   OR e.event_name LIKE ?
                ORDER BY r.generated_at DESC
                """;

        List<ReportHistoryRecord> reports = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    reports.add(new ReportHistoryRecord(
                            rs.getInt("report_id"),
                            rs.getString("report_name"),
                            rs.getString("report_type"),
                            rs.getString("event_name"),
                            rs.getString("file_format"),
                            rs.getInt("record_count"),
                            rs.getTimestamp("generated_at"),
                            rs.getString("file_path")
                    ));
                }
            }
        }

        return reports;
    }
    // Update exported report file location
    public void updateFilePath(int reportId, String filePath)
            throws SQLException {

        String sql = """
            UPDATE REPORT_HISTORY
            SET file_path = ?
            WHERE report_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, filePath);
            ps.setInt(2, reportId);

            ps.executeUpdate();
        }
    }
}
