package com.moviebooking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/movie_booking_system"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Kolkata";

    private static final String USER = "root";
    private static final String PASSWORD = "Rakshitha";

    public static Connection getConnection() {

        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Database Connected Successfully");

        } catch (ClassNotFoundException e) {

            System.out.println(
                    "MySQL JDBC driver not found on the classpath."
            );

            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println(
                    "Could not connect to the database - "
                    + "check that MySQL is running, "
                    + "and that the username/password/database name are correct."
            );

            e.printStackTrace();
        }

        return con;
    }
}
