
package com.csr.service;

import com.csr.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {

    public static class AuthenticatedUser {

        private final int id;
        private final String username;
        private final String name;
        private final String role;

        public AuthenticatedUser(
                int id, String username, String name, String role) {

            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
    }

    public AuthenticatedUser authenticate(
            String username, String password) throws SQLException {

        String sql =
                "SELECT id, username, name, email, password, role " +
                        "FROM USERS " +
                        "WHERE (username = ? OR email = ?) " +
                        "AND is_active = 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // Set both SQL parameters
            statement.setString(1, username);
            statement.setString(2, username);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                String storedPassword = rs.getString("password");

                if (!PasswordUtil.verifyPassword(
                        password, storedPassword)) {

                    return null;
                }

                return new AuthenticatedUser(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("role")
                );
            }
        }
    }
}
