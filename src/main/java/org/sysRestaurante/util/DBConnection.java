package org.sysRestaurante.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class DBConnection {

    private static final Logger LOGGER =
            LoggerHandler.getGenericConsoleHandler(DBConnection.class.getName());

    private static final String JDBC_PREFIX = "jdbc:sqlite:";

    private static final boolean IS_PRODUCTION =
            Boolean.parseBoolean(System.getProperty("sys.production", "true"));

    private static final String DB_RESOURCE =
            IS_PRODUCTION ? "/external/production.db" : "/external/devel.db";

    private static final Path DB_PATH =
            Paths.get(System.getProperty("user.home"),
                    ".sysRestaurante",
                    IS_PRODUCTION ? "production.db" : "devel.db");

    private static int globalDBRequestsCount = 0;

    static {
        try {
            initializeDatabase();
            LOGGER.info("Database ready at: " + DB_PATH);
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize database: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void initializeDatabase() throws Exception {
        if (Files.exists(DB_PATH)) {
            return;
        }

        Files.createDirectories(DB_PATH.getParent());

        try (InputStream in = DBConnection.class.getResourceAsStream(DB_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Database resource not found: " + DB_RESOURCE);
            }
            Files.copy(in, DB_PATH);
        }

        DBInitializer.initDatabase(DB_PATH.toString());
    }

    public static Connection getConnection() throws SQLException {
        LOGGER.config("New DB request");
        incrementGlobalDBRequestsCount();
        return DriverManager.getConnection(JDBC_PREFIX + DB_PATH.toAbsolutePath());
    }

    public static int getGlobalDBRequestsCount() {
        return globalDBRequestsCount;
    }

    private static void incrementGlobalDBRequestsCount() {
        globalDBRequestsCount++;
    }
}
