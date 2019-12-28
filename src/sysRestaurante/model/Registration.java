package sysRestaurante.model;

import sysRestaurante.util.DBConnection;
import sysRestaurante.util.Encryption;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Registration {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Registration.class.getName());

    public void userRegister(String nome, String pass, String username, String email) throws SQLException {

        PreparedStatement ps = null;

        String query = "INSERT INTO usuario (nome, senha, username, email, isAdmin) VALUES (?, ?, ?, ?, ?)";
        String password = Encryption.encrypt(pass);

        try {
            Connection con = DBConnection.getConnection();
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
        }
    }
}
