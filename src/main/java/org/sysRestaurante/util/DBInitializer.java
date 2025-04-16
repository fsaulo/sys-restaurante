package org.sysRestaurante.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

public class DBInitializer {
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DBInitializer.class.getName());
    private static final String SCHEMA_FILE = "src/main/resources/external/schema.sql";
    private static final String DB_URL = "jdbc:sqlite:";

    public static void initDatabase(String dbFilePath) {
        if (isDBInitialized(dbFilePath)) {
            return;
        }

        String database = DB_URL + dbFilePath;
        try (Connection conn = DriverManager.getConnection(database)) {
            if (conn == null) {
                return;
            }

            String sql = readSchemaFile();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("PRAGMA foreign_keys = ON;");
            stmt.executeUpdate(sql);

            LOGGER.info("Database created using file: " + SCHEMA_FILE);
        } catch (SQLException | IOException e) {
            LOGGER.severe("Error initializing database: " + e.getMessage());
        }
    }

    private static boolean isDBInitialized(String dbFilePath) {
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            return false;
        }

        String database = DB_URL + dbFilePath;
        try (Connection conn = DriverManager.getConnection(database)) {
            String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='metadata'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            LOGGER.severe("Error during database initialization" + e.getMessage());
        }

        return false;
    }

    private static String readSchemaFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(SCHEMA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
