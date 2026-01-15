package org.sysRestaurante.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.logging.Logger;

public final class DBInitializer {

    private static final Logger LOGGER =
            LoggerHandler.getGenericConsoleHandler(DBInitializer.class.getName());

    private static final String SCHEMA_RESOURCE = "/external/schema.sql";
    private static final String JDBC_PREFIX = "jdbc:sqlite:";

    public static void initDatabase(String dbFilePath) {
        if (isDBInitialized(dbFilePath)) {
            return;
        }

        String jdbcUrl = JDBC_PREFIX + dbFilePath;

        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);
            stmt.execute("PRAGMA foreign_keys = ON");

            for (String sql : loadSchemaStatements()) {
                if (!sql.isBlank()) {
                    stmt.execute(sql);
                }
            }

            conn.commit();
            LOGGER.info("Database initialized using schema: " + SCHEMA_RESOURCE);

        } catch (Exception e) {
            LOGGER.severe("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static boolean isDBInitialized(String dbFilePath) {
        String jdbcUrl = JDBC_PREFIX + dbFilePath;

        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?")) {

            ps.setString(1, "metadata");

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            LOGGER.warning("Database not initialized yet: " + e.getMessage());
            return false;
        }
    }

    private static String[] loadSchemaStatements() throws Exception {
        try (InputStream in = DBInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Schema resource not found: " + SCHEMA_RESOURCE);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br =
                         new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }

            return sb.toString().split(";");
        }
    }
}
