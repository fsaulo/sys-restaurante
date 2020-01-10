package org.sysRestaurante.model;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.etc.Employee;
import org.sysRestaurante.etc.Manager;
import org.sysRestaurante.etc.User;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.Encryption;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

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
    private Connection con;

    public boolean isDatabaseConnected() {
        try {
            con = DBConnection.getConnection();
            if (con.isClosed()) {
                return false;
            } else {
                con.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateSessionTable(int userId) throws SQLException {
        LocalDateTime date = LocalDateTime.now();
        boolean isConnectionNew = false;
        PreparedStatement ps;
        String query = "INSERT INTO sessao (id_usuario, data_sessao, tempo_sessao) VALUES (?, ?, ?)";

        if (con == null || con.isClosed()) {
            con = DBConnection.getConnection();
            isConnectionNew = true;
        }

        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date.toLocalDate()));
            ps.setTime(3, java.sql.Time.valueOf(date.toLocalTime()));
            ps.executeUpdate();
            ps.close();

            if (isConnectionNew) {
                con.close();
            }

            LOGGER.setLevel(Level.ALL);
            LOGGER.config("Session time: " + DATE_FORMAT.format(date));
        } catch (SQLException ex) {
            LOGGER.severe("Session couldn't be stored.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }

    public int loginRequested(String user, String pass) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM usuario WHERE username = ? and senha = ?";
        String password = Encryption.encrypt(pass);

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id_usuario");
                AppFactory appFactory = new AppFactory();
                if (!rs.getBoolean("is_admin")) {
                    appFactory.setUser(new Employee(
                            rs.getString("nome"),
                            rs.getString("senha"),
                            rs.getString("username"),
                            rs.getString("email")
                    ));
                    updateSessionTable(userId);
                    return 0;
                }
                else if (rs.getBoolean("is_admin")) {
                    updateSessionTable(userId);
                    appFactory.setUser(new Manager(
                            rs.getString("nome"),
                            rs.getString("senha"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getBoolean("is_admin")
                    ));
                    return 1;
                }
            } else return 2;
        } finally {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
            if (con != null) con.close();
        }
        return 1;
    }

    public User getUserData(String username) {
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM usuario WHERE username = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();
            User user = new User();

            while (rs.next()) {
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setName(rs.getString("nome"));
                user.setUsername(username);
                user.setEmail(rs.getString("email"));
                user.setAdmin(rs.getBoolean("is_admin"));
            }
            ps.close();
            rs.close();
            con.close();
            return user;
        } catch (SQLException e) {
            LOGGER.severe("Couldn't get user data.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            e.printStackTrace();
        }
        return null;
    }



    public void setSessionDuration(int userId, int lastSessionID, long sessionTime) {
        PreparedStatement ps;
        String query = "UPDATE sessao SET duracao_sessao = ? WHERE id_usuario = ? and id_sessao = ?";

        try  {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, (int) sessionTime);
            ps.setInt(2, userId);
            ps.setInt(3, lastSessionID);
            ps.executeUpdate();
            ps.close();
            con.close();

            LOGGER.info("Last session duration registered.");
        } catch (SQLException e) {
            LOGGER.severe("Couldn't register session duration.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            e.printStackTrace();
        }
    }

    public LocalDateTime getLastSessionDate() {
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM sessao";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            ArrayList<Long> dates = new ArrayList<>();

            while (rs.next()) {
                dates.add(rs.getDate("data_sessao").getTime()
                        + rs.getTime("tempo_sessao").getTime()
                        - 10800000L);
            }
            ps.close();
            rs.close();
            con.close();

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

    public int getLastSessionId() {
        PreparedStatement ps;
        String query = "SELECT id_sessao FROM sessao ORDER BY id_sessao DESC LIMIT 1";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt("id_sessao");
            }

            ps.close();
            rs.close();
            con.close();
            return id;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
