package org.sysRestaurante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DBConnection.class.getName());
    private static final String DB_URL = "jdbc:sqlite:";
    private static final String DB_LOCAL_CONNECTION_PRODUCTION = "src/main/resources/external/production.db";
    private static final String DB_LOCAL_CONNECTION_DEV = "src/main/resources/external/devel.db";
    private static final String DB_LOCAL_CONNECTION;
    private static int globalDBRequestsCount = 0;

    static {
        boolean isProduction = Boolean.parseBoolean(System.getProperty("app.env.production", "true"));
        DB_LOCAL_CONNECTION = isProduction ? DB_LOCAL_CONNECTION_PRODUCTION : DB_LOCAL_CONNECTION_DEV;
        DBInitializer.initDatabase(DB_LOCAL_CONNECTION);
        LOGGER.info("Database was initialized (production = " + isProduction + ")");
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            LOGGER.config("New request.");
            String dbFilePath = DB_URL + DB_LOCAL_CONNECTION;
            Connection con = DriverManager.getConnection(dbFilePath);
            assert con != null;
            DBConnection.incrementGlobalDBRequestsCount();
            return con;
        } catch (ClassNotFoundException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Database driver not found: " + ex.getMessage());
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
