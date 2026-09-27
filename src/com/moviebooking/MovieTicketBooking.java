package com.moviebooking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

public class MovieTicketBooking {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Connection con = DBConnection.getConnection();

        if (con == null) {
            System.out.println("Database connection failed.");
            sc.close();
            return;
        }

        int choice;

        do {

            System.out.println("\n===== MOVIE TICKET BOOKING SYSTEM =====");
            System.out.println("1. Movie Listing");
            System.out.println("2. Theater Management");
            System.out.println("3. Seat Booking");
            System.out.println("4. Ticket Generation");
            System.out.println("5. Exit");
            System.out.print("\nEnter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number.");
                choice = 0;
                continue;
            }

            switch (choice) {

                case 1:
                    movieListing(con, sc);
                    break;

                case 2:
                    theaterManagement(con, sc);
                    break;

                case 3:
                    seatBooking(con, sc);
                    break;

                case 4:
                    ticketGeneration(con, sc);
                    break;

                case 5:
                    System.out.println("\nThank you for using the system.");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }

        } while (choice != 5);

        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("Error while closing database connection.");
        }

        sc.close();
    }

    public static void movieListing(Connection con, Scanner sc) {

        System.out.println("\n========== MOVIE LISTING ==========");

        try {

            String sql =
                    "SELECT * FROM movies ORDER BY movie_id";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                System.out.printf(
                        "%-5s %-30s %-15s %-10s%n",
                        "ID",
                        "TITLE",
                        "GENRE",
                        "DURATION"
                );

                System.out.println(
                        "--------------------------------------------------------------"
                );

                while (rs.next()) {

                    System.out.printf(
                            "%-5d %-30s %-15s %-10s%n",
                            rs.getInt("movie_id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getInt("duration_minutes") + " min"
                    );
                }
            }

            System.out.print("\nAdd a new movie? (y/n): ");
            String choice = sc.nextLine().trim();

            if (!choice.equalsIgnoreCase("y")) {
                return;
            }

            System.out.print("Enter title: ");
            String title = sc.nextLine().trim();

            if (title.isEmpty()) {
                System.out.println(
                        "\nTitle cannot be empty. Movie not added."
                );
                return;
            }

            System.out.print("Enter genre: ");
            String genre = sc.nextLine().trim();

            if (genre.isEmpty()) {
                System.out.println(
                        "\nGenre cannot be empty. Movie not added."
                );
                return;
            }

            System.out.print("Enter duration (minutes): ");

            int duration;

            try {
                duration = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(
                        "\nPlease enter only the number of minutes."
                );
                System.out.println("Movie not added.");
                return;
            }

            if (duration <= 0) {
                System.out.println(
                        "\nDuration must be greater than 0."
                );
                System.out.println("Movie not added.");
                return;
            }

            String insertSql =
                    "INSERT INTO movies "
                    + "(title, genre, duration_minutes) "
                    + "VALUES (?, ?, ?)";

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 insertSql,
                                 Statement.RETURN_GENERATED_KEYS
                         )) {

                ps.setString(1, title);
                ps.setString(2, genre);
                ps.setInt(3, duration);

                ps.executeUpdate();

                int movieId = 0;

                try (ResultSet keys = ps.getGeneratedKeys()) {

                    if (keys.next()) {
                        movieId = keys.getInt(1);
                    }
                }

                System.out.println(
                        "\nMovie added successfully."
                );

                System.out.println(
                        "Movie ID: " + movieId
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "\nError while managing movie listing."
            );

            e.printStackTrace();
        }
    }

    public static void theaterManagement(
            Connection con,
            Scanner sc) {

        System.out.println(
                "\n========== THEATER MANAGEMENT =========="
        );

        try {

            String sql =
                    "SELECT * FROM theaters ORDER BY theater_id";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                System.out.printf(
                        "%-5s %-25s %-20s %-10s%n",
                        "ID",
                        "NAME",
                        "LOCATION",
                        "SEATS"
                );

                System.out.println(
                        "--------------------------------------------------------------"
                );

                while (rs.next()) {

                    System.out.printf(
                            "%-5d %-25s %-20s %-10d%n",
                            rs.getInt("theater_id"),
                            rs.getString("theater_name"),
                            rs.getString("location"),
                            rs.getInt("total_seats")
                    );
                }
            }

            System.out.print(
                    "\nAdd a new theater? (y/n): "
            );

            String addTheater =
                    sc.nextLine().trim();

            if (addTheater.equalsIgnoreCase("y")) {

                addTheater(con, sc);
            }

            System.out.print(
                    "\nSchedule a new show? (y/n): "
            );

            String addShow =
                    sc.nextLine().trim();

            if (addShow.equalsIgnoreCase("y")) {

                scheduleShow(con, sc);
            }

        } catch (SQLException e) {

            System.out.println(
                    "\nError while managing theaters."
            );

            e.printStackTrace();
        }
    }

    private static void addTheater(
            Connection con,
            Scanner sc) throws SQLException {

        System.out.print("Enter theater name: ");

        String name =
                sc.nextLine().trim();

        if (name.isEmpty()) {

            System.out.println(
                    "\nTheater name cannot be empty. Not added."
            );

            return;
        }

        System.out.print("Enter location: ");

        String location =
                sc.nextLine().trim();

        if (location.isEmpty()) {

            System.out.println(
                    "\nLocation cannot be empty. Not added."
            );

            return;
        }

        System.out.print("Enter total seats: ");

        int totalSeats;

        try {

            totalSeats =
                    Integer.parseInt(
                            sc.nextLine().trim()
                    );

        } catch (NumberFormatException e) {

            System.out.println(
                    "\nPlease enter a valid number. Not added."
            );

            return;
        }

        if (totalSeats <= 0) {

            System.out.println(
                    "\nTotal seats must be greater than 0. Not added."
            );

            return;
        }

        String insertSql =
                "INSERT INTO theaters "
                + "(theater_name, location, total_seats) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(
                             insertSql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            ps.setString(1, name);
            ps.setString(2, location);
            ps.setInt(3, totalSeats);

            ps.executeUpdate();

            int theaterId = 0;

            try (ResultSet keys =
                         ps.getGeneratedKeys()) {

                if (keys.next()) {
                    theaterId = keys.getInt(1);
                }
            }

            System.out.println(
                    "\nTheater added successfully."
            );

            System.out.println(
                    "Theater ID: " + theaterId
            );
        }
    }
    
    private static void scheduleShow(
            Connection con,
            Scanner sc) throws SQLException {

        System.out.println("\nAvailable movies:");

        try (Statement st = con.createStatement();
             ResultSet rs =
                     st.executeQuery(
                             "SELECT movie_id, title "
                             + "FROM movies "
                             + "ORDER BY movie_id"
                     )) {

            while (rs.next()) {

                System.out.println(
                        "  "
                        + rs.getInt("movie_id")
                        + " - "
                        + rs.getString("title")
                );
            }
        }

        System.out.print("Enter movie_id: ");

        int movieId;

        try {

            movieId =
                    Integer.parseInt(
                            sc.nextLine().trim()
                    );

        } catch (NumberFormatException e) {

            System.out.println(
                    "\nPlease enter a valid movie_id. "
                    + "Show not scheduled."
            );

            return;
        }

        String movieCheck =
                "SELECT title FROM movies "
                + "WHERE movie_id = ?";

        try (PreparedStatement ps =
                     con.prepareStatement(movieCheck)) {

            ps.setInt(1, movieId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (!rs.next()) {

                    System.out.println(
                            "\nNo movie found with id "
                            + movieId
                            + ". Show not scheduled."
                    );

                    return;
                }
            }
        }

        System.out.println("\nAvailable theaters:");

        try (Statement st = con.createStatement();
             ResultSet rs =
                     st.executeQuery(
                             "SELECT theater_id, theater_name, "
                             + "total_seats FROM theaters "
                             + "ORDER BY theater_id"
                     )) {

            while (rs.next()) {

                System.out.println(
                        "  "
                        + rs.getInt("theater_id")
                        + " - "
                        + rs.getString("theater_name")
                        + " ("
                        + rs.getInt("total_seats")
                        + " seats)"
                );
            }
        }

        System.out.print("Enter theater_id: ");

        int theaterId;

        try {

            theaterId =
                    Integer.parseInt(
                            sc.nextLine().trim()
                    );

        } catch (NumberFormatException e) {

            System.out.println(
                    "\nPlease enter a valid theater_id. "
                    + "Show not scheduled."
            );

            return;
        }

        int totalSeats;

        String theaterCheck =
                "SELECT theater_name, total_seats "
                + "FROM theaters "
                + "WHERE theater_id = ?";

        try (PreparedStatement ps =
                     con.prepareStatement(theaterCheck)) {

            ps.setInt(1, theaterId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (!rs.next()) {

                    System.out.println(
                            "\nNo theater found with id "
                            + theaterId
                            + ". Show not scheduled."
                    );

                    return;
                }

                totalSeats =
                        rs.getInt("total_seats");
            }
        }

        System.out.print(
                "Enter show time "
                + "(yyyy-MM-dd HH:mm:ss): "
        );

        String timeInput =
                sc.nextLine().trim();

        Timestamp showTime;

        try {

            showTime =
                    Timestamp.valueOf(timeInput);

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "\nInvalid date/time format."
            );

            System.out.println(
                    "Use exactly: yyyy-MM-dd HH:mm:ss"
            );

            System.out.println(
                    "Example: 2026-10-30 14:30:00"
            );

            System.out.println(
                    "Show not scheduled."
            );

            return;
        }

        String insertSql =
                "INSERT INTO shows "
                + "(movie_id, theater_id, show_time, "
                + "available_seats) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(
                             insertSql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            ps.setInt(1, movieId);
            ps.setInt(2, theaterId);
            ps.setTimestamp(3, showTime);
            ps.setInt(4, totalSeats);

            ps.executeUpdate();

            int showId = 0;

            try (ResultSet keys =
                         ps.getGeneratedKeys()) {

                if (keys.next()) {
                    showId = keys.getInt(1);
                }
            }

            System.out.println(
                    "\nShow scheduled successfully."
            );

            System.out.println(
                    "Show ID: "
                    + showId
                    + " ("
                    + totalSeats
                    + " seats available)"
            );
        }
    }

    public static void seatBooking(
            Connection con,
            Scanner sc) {

        boolean transactionStarted = false;

        try {

            System.out.println(
                    "\n========== SEAT BOOKING =========="
            );

            String showsSql =
                    "SELECT sh.show_id, "
                    + "m.title, "
                    + "t.theater_name, "
                    + "t.location, "
                    + "sh.show_time, "
                    + "sh.available_seats "
                    + "FROM shows sh "
                    + "JOIN movies m "
                    + "ON m.movie_id = sh.movie_id "
                    + "JOIN theaters t "
                    + "ON t.theater_id = sh.theater_id "
                    + "WHERE sh.show_time >= NOW() "
                    + "ORDER BY sh.show_id";

            try (Statement st =
                         con.createStatement();
                 ResultSet rs =
                         st.executeQuery(showsSql)) {

                System.out.println(
                        "\n========================== UPCOMING SHOWS =========================="
                );

                System.out.printf(
                        "%-5s %-20s %-18s %-15s %-20s %-10s%n",
                        "ID",
                        "MOVIE",
                        "THEATER",
                        "LOCATION",
                        "SHOW TIME",
                        "SEATS"
                );

                System.out.println(
                        "--------------------------------------------------------------------"
                );

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    System.out.printf(
                            "%-5d %-20s %-18s %-15s %-20s %-10d%n",
                            rs.getInt("show_id"),
                            rs.getString("title"),
                            rs.getString("theater_name"),
                            rs.getString("location"),
                            rs.getTimestamp("show_time"),
                            rs.getInt("available_seats")
                    );
                }

                System.out.println(
                        "--------------------------------------------------------------------"
                );

                if (!found) {

                    System.out.println(
                            "No upcoming shows available."
                    );

                    return;
                }
            }

            System.out.print(
                    "\nEnter show_id: "
            );

            int showId;

            try {

                showId =
                        Integer.parseInt(
                                sc.nextLine().trim()
                        );

            } catch (NumberFormatException e) {

                System.out.println(
                        "\nPlease enter a valid show_id."
                );

                return;
            }

            System.out.print(
                    "Enter customer name: "
            );

            String customerName =
                    sc.nextLine().trim();

            if (customerName.isEmpty()) {

                System.out.println(
                        "\nCustomer name cannot be empty. "
                        + "Booking cancelled."
                );

                return;
            }

            System.out.print(
                    "Enter number of seats to book: "
            );

            int seatsRequested;

            try {

                seatsRequested =
                        Integer.parseInt(
                                sc.nextLine().trim()
                        );

            } catch (NumberFormatException e) {

                System.out.println(
                        "\nPlease enter a valid number."
                );

                return;
            }

            if (seatsRequested <= 0) {

                System.out.println(
                        "\nNumber of seats must be "
                        + "greater than 0."
                );

                return;
            }

            con.setAutoCommit(false);

            transactionStarted = true;

            String lockSql =
                    "SELECT available_seats "
                    + "FROM shows "
                    + "WHERE show_id = ? "
                    + "FOR UPDATE";

            try (PreparedStatement psLock =
                         con.prepareStatement(lockSql)) {

                psLock.setInt(1, showId);

                try (ResultSet rs =
                             psLock.executeQuery()) {

                    if (!rs.next()) {

                        System.out.println(
                                "\nNo show found with id "
                                + showId
                                + ". Booking cancelled."
                        );

                        con.rollback();

                        return;
                    }

                    int availableSeats =
                            rs.getInt("available_seats");

                    if (seatsRequested >
                            availableSeats) {

                        System.out.println(
                                "\nOnly "
                                + availableSeats
                                + " seat(s) available "
                                + "for show #"
                                + showId
                                + "."
                        );

                        System.out.println(
                                "Cannot book "
                                + seatsRequested
                                + " seat(s). "
                                + "Booking cancelled."
                        );

                        con.rollback();

                        return;
                    }
                }
            }

            String updateSql =
                    "UPDATE shows "
                    + "SET available_seats = "
                    + "available_seats - ? "
                    + "WHERE show_id = ?";

            try (PreparedStatement psUpdate =
                         con.prepareStatement(updateSql)) {

                psUpdate.setInt(
                        1,
                        seatsRequested
                );

                psUpdate.setInt(
                        2,
                        showId
                );

                psUpdate.executeUpdate();
            }

            String bookingSql =
                    "INSERT INTO bookings "
                    + "(show_id, customer_name, seats_booked) "
                    + "VALUES (?, ?, ?)";

            int bookingId = 0;

            try (PreparedStatement psBooking =
                         con.prepareStatement(
                                 bookingSql,
                                 Statement.RETURN_GENERATED_KEYS
                         )) {

                psBooking.setInt(
                        1,
                        showId
                );

                psBooking.setString(
                        2,
                        customerName
                );

                psBooking.setInt(
                        3,
                        seatsRequested
                );

                psBooking.executeUpdate();

                try (ResultSet keys =
                             psBooking.getGeneratedKeys()) {

                    if (keys.next()) {

                        bookingId =
                                keys.getInt(1);
                    }
                }
            }

            con.commit();

            System.out.println(
                    "\n============================================"
            );

            System.out.println(
                    "       BOOKING CONFIRMED"
            );

            System.out.println(
                    "============================================"
            );

            System.out.println(
                    "Booking ID   : " + bookingId
            );

            System.out.println(
                    "Customer     : " + customerName
            );

            System.out.println(
                    "Seats Booked : " + seatsRequested
            );

            System.out.println(
                    "============================================"
            );

            System.out.println(
                    "Use this Booking ID with Ticket Generation "
                    + "to print your ticket."
            );

        } catch (SQLException e) {

            System.out.println(
                    "\nError while booking seats. "
                    + "Rolling back."
            );

            if (transactionStarted) {

                rollbackQuietly(con);
            }

            e.printStackTrace();

        } finally {

            try {

                con.setAutoCommit(true);

            } catch (SQLException e) {

                System.out.println(
                        "Could not reset transaction mode."
                );
            }
        }
    }

    public static void ticketGeneration(
            Connection con,
            Scanner sc) {

        System.out.println(
                "\n========== TICKET GENERATION =========="
        );

        System.out.print(
                "Enter booking_id: "
        );

        int bookingId;

        try {

            bookingId =
                    Integer.parseInt(
                            sc.nextLine().trim()
                    );

        } catch (NumberFormatException e) {

            System.out.println(
                    "\nPlease enter a valid booking_id."
            );

            return;
        }

        String sql =
                "SELECT "
                + "b.customer_name, "
                + "b.seats_booked, "
                + "b.booking_time, "
                + "m.title, "
                + "m.genre, "
                + "m.duration_minutes, "
                + "t.theater_name, "
                + "t.location, "
                + "sh.show_time "
                + "FROM bookings b "
                + "JOIN shows sh "
                + "ON sh.show_id = b.show_id "
                + "JOIN movies m "
                + "ON m.movie_id = sh.movie_id "
                + "JOIN theaters t "
                + "ON t.theater_id = sh.theater_id "
                + "WHERE b.booking_id = ?";

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (!rs.next()) {

                    System.out.println(
                            "\nNo booking found with id "
                            + bookingId
                    );

                    return;
                }

                System.out.println(
                        "\n================ TICKET ================"
                );

                System.out.println(
                        "Booking ID   : " + bookingId
                );

                System.out.println(
                        "Movie        : "
                        + rs.getString("title")
                        + " ("
                        + rs.getString("genre")
                        + ", "
                        + rs.getInt("duration_minutes")
                        + " min)"
                );

                System.out.println(
                        "Theater      : "
                        + rs.getString("theater_name")
                        + " - "
                        + rs.getString("location")
                );

                System.out.println(
                        "Show Time    : "
                        + rs.getTimestamp("show_time")
                );

                System.out.println(
                        "-----------------------------------------"
                );

                System.out.println(
                        "Customer     : "
                        + rs.getString("customer_name")
                );

                System.out.println(
                        "Seats Booked : "
                        + rs.getInt("seats_booked")
                );

                System.out.println(
                        "Booked At    : "
                        + rs.getTimestamp("booking_time")
                );

                System.out.println(
                        "========================================="
                );

                System.out.println(
                        "        Enjoy your movie!"
                );

                System.out.println(
                        "========================================="
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "\nError while generating ticket."
            );

            e.printStackTrace();
        }
    }

    private static void rollbackQuietly(
            Connection con) {

        try {

            con.rollback();

        } catch (SQLException e) {

            System.out.println(
                    "Unable to rollback transaction."
            );
        }
    }
}