package sysRestaurante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DBConnection.class.getName());

    private static final String DB_LOCAL_CONNECTION = "jdbc:sqlite:resources/db/sys_restaurante.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_LOCAL_CONNECTION);
        } catch (ClassNotFoundException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Database driver not found");
            ex.printStackTrace();
        }
        return null;
    }
}
