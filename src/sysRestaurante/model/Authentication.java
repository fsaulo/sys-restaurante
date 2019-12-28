package sysRestaurante.model;

import sysRestaurante.util.DBConnection;
import sysRestaurante.util.Encryption;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Authentication {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Authentication.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static Connection con;

    public Authentication() {
        try {
            con = DBConnection.getConnection();
            if (con != null)
                LOGGER.info("Successful connection to the database.");

        } catch (SQLException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Connection to database couldn't be established.");
            ex.printStackTrace();
        }
    }

    public boolean isDatabaseConnected() {
        return con != null;
    }

    public int systemAuthentication(String user, String pass, boolean isAdmin) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM usuario WHERE username = ? and senha = ?";

        String password = Encryption.encrypt(pass);

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("IdUsuario");

                if (!rs.getBoolean("isAdmin") && !isAdmin) {
                    updateSessionTable(userId);
                    return 0;
                }
                else if (rs.getBoolean("isAdmin")) {
                    updateSessionTable(userId);
                    return 1;
                }
                else if (!rs.getBoolean("isAdmin") && isAdmin) {
                    return 3;
                }
            } else return 2;

        } finally {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
        return 1;
    }

    public void updateSessionTable(int userId) {

        LocalDateTime date = LocalDateTime.now();

        String query = "INSERT INTO sessao (idUsuario, dataSessao, tempoSessao) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date.toLocalDate()));
            ps.setTime(3, java.sql.Time.valueOf(date.toLocalTime()));
            ps.executeUpdate();

            LOGGER.setLevel(Level.ALL);
            LOGGER.config("Session time: " + DATE_FORMAT.format(date));

        } catch (SQLException ex) {
            LOGGER.severe("Session couldn't be stored.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }

    public LocalDateTime getLastSessionDate() {

        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM sessao";

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            ArrayList<Long> dates = new ArrayList<>();

            while (rs.next()) {
                dates.add(rs.getDate("dataSessao").getTime()
                        + rs.getTime("tempoSessao").getTime()
                        - 10800000L);
            }

            if (dates.isEmpty()) {
                return null;
            } else {
                Long mostRecentSessionLong = Collections.max(dates);
                LocalDateTime mostRecentSession = Instant.ofEpochMilli(mostRecentSessionLong)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                return mostRecentSession;
            }
        } catch (SQLException | NullPointerException ex) {
            LOGGER.severe("Error while getting last session.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
        return null;
    }
}
