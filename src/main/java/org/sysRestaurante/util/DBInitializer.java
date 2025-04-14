package org.sysRestaurante.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {
    private static final String SCHEMA_FILE = "src/main/resources/external/schema.sql";
    private static final String DB_URL = "jdbc:sqlite:";

    public static void initDatabase(String dbFilePath) {
        File dbFile = new File(dbFilePath);

        if (!dbFile.exists()) {
            String database = DB_URL + dbFilePath;
            try (Connection conn = DriverManager.getConnection(database)) {
                if (conn != null) {
                    String sql = readSchemaFile(SCHEMA_FILE);
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("PRAGMA foreign_keys = ON;");
                        stmt.executeUpdate(sql);
                        System.out.println("Database created using schema.sql.");
                    }
                }
            } catch (SQLException | IOException e) {
                System.out.println("Error initializing database: " + e.getMessage());
            }
        } else {
            System.out.println("Database already exists, skipping initialization.");
        }
    }

    private static String readSchemaFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
