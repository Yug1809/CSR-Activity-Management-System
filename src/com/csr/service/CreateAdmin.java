
package com.csr.service;

import com.csr.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateAdmin {

    public static void main(String[] args) throws Exception {

        String username = "admin";
        String name = "System Administrator";
        String email = "admin@csr.local";
        String password = "admin123";

        String checkSql =
                "SELECT id FROM USERS WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check =
                     con.prepareStatement(checkSql)) {

            check.setString(1, username);

            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    System.out.println(
                            "Admin username already exists. No changes made."
                    );
                    return;
                }
            }

            String hash = PasswordUtil.hashPassword(password);

            String insertSql = """
                    INSERT INTO USERS (username, name, email, password, role)
                    VALUES (?, ?, ?, ?, 'Admin')
                    """;

            try (PreparedStatement insert =
                         con.prepareStatement(insertSql)) {

                insert.setString(1, username);
                insert.setString(2, name);
                insert.setString(3, email);
                insert.setString(4, hash);

                insert.executeUpdate();

                System.out.println("Admin account created successfully.");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
            }
        }
    }
}
