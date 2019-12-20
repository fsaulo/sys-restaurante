package sysRestaurante.model;

import sysRestaurante.util.DBConnection;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Authentication {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Authentication.class.getName());

    private Connection con;

    public Authentication() {
        try {
            con = DBConnection.getConnection();
        } catch (SQLException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Connection to database couldn't be established.");
            ex.printStackTrace();
        }
    }

    public boolean isDatabaseConnected() {
        return con != null;
    }

    public String getUsername() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM usuario WHERE id_usuario = ?";

        try {

            ps = con.prepareStatement(query);
            ps.setInt(1, 2);

            rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("nome");

        } finally {
            if (ps !=  null) ps.close();
            if (rs != null) rs.close();
        }
        return null;
    }
}
