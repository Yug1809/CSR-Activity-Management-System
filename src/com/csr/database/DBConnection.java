package com.csr.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/csr_management";

    private static final String USER =
            System.getenv("CSR_DB_USER");

    private static final String PASSWORD =
            System.getenv("CSR_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (USER == null || USER.isBlank()) {
            throw new SQLException(
                    "Database username not configured. Set CSR_DB_USER environment variable."
            );
        }

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Database password not configured. Set CSR_DB_PASSWORD environment variable."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}