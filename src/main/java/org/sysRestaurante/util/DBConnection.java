package org.sysRestaurante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DBConnection.class.getName());
    private static final String DB_LOCAL_CONNECTION = "jdbc:sqlite:src/main/resources/external/sys_restaurante.db";
    private static int globalDBRequestsCount = 0;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            LOGGER.config("New request.");
            Connection con = DriverManager.getConnection(DB_LOCAL_CONNECTION);
            assert con != null;
            DBConnection.incrementGlobalDBRequestsCount();
            return con;
        } catch (ClassNotFoundException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Database driver not found");
            ex.printStackTrace();
        }
        return null;
    }

    public static int getGlobalDBRequestsCount() {
        return DBConnection.globalDBRequestsCount;
    }

    public static void incrementGlobalDBRequestsCount() {
        DBConnection.globalDBRequestsCount += 1;
    }
}
