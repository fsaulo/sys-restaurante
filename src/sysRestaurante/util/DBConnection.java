package sysRestaurante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DBConnection.class.getName());

    private static final String DB_LOCAL_CONNECTION = "jdbc:sqlite:resources/external/sys_restaurante.db";

    private static Connection con = null;

    public static Connection getConnection() throws SQLException {

        try {

            Class.forName("org.sqlite.JDBC");

            if (con == null) {
                LOGGER.info("Trying to acquire connection with database circuit...");
                con = DriverManager.getConnection(DB_LOCAL_CONNECTION);
            }

            return con;

        } catch (ClassNotFoundException ex) {

            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Database driver not found");
            ex.printStackTrace();

        }
        return null;
    }
}
