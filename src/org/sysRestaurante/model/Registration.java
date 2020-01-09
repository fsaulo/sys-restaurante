package org.sysRestaurante.model;

import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.Encryption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Registration {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Registration.class.getName());

    public void insert(String nome, String pass, String username, String email) throws SQLException {
        PreparedStatement ps = null;
        Connection con = null;
        String query = "INSERT INTO usuario (nome, senha, username, email, isAdmin) VALUES (?, ?, ?, ?, ?)";
        String password = Encryption.encrypt(pass);

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, nome);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.setString(4, email);
            ps.setBoolean(5, false);
            ps.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.severe("User couldn't be registered.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();

        } finally {
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }
}
