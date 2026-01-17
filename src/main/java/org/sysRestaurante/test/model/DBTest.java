import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.DBInitializer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class DBTest {
    private static final Path DB_PATH =
            Paths.get(System.getProperty("user.home"),
                    ".sysRestaurante", "devel.db");
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH.toAbsolutePath();

    @BeforeEach
    public void setup() {
        File dbFile = new File(DB_PATH.toAbsolutePath().toUri());
        if (dbFile.exists()) {
            assertTrue(dbFile.delete(), "Failed to delete existing DB file before test.");
        }
    }

    @AfterEach
    public void cleanup() {
        File dbFile = new File(DB_PATH.toUri());
        if (dbFile.exists()) {
            assertTrue(dbFile.delete(), "Failed to delete DB file after test.");
        }
    }

    @Test
    public void shouldCreateDBwithTables() throws SQLException {

        DBInitializer.initDatabase(DB_PATH.toAbsolutePath());

        File dbFile = new File(DB_PATH.toUri());
        assertTrue(dbFile.exists(), "Database file was not created.");

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            ResultSet rsMetadata = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='metadata'"
            );
            assertTrue(rsMetadata.next(), "'metadata' table was not created.");

            ResultSet rsUsers = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='usuario'"
            );
            assertTrue(rsUsers.next(), "'usuario' table was not created.");

            ResultSet rsAdminUser = stmt.executeQuery(
                    "SELECT * FROM usuario WHERE username='admin'"
            );
            assertTrue(rsAdminUser.next(), "Admin user not found in 'usuario' table.");
        }
    }

    @Test
    public void shouldGetDBConnection() throws SQLException {
        Connection conn = DBConnection.getConnection();
        assertNotNull(conn, "Connection is null");
    }
}
