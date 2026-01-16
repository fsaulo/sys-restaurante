import org.junit.jupiter.api.Test;
import org.sysRestaurante.util.DBInitializer;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ResourcesTest {

    @Test
    void schemaSqlMustBeOnClasspath() {
        URL resource = DBInitializer.class.getResource("/external/schema.sql");

        assertNotNull(
                resource,
                "schema.sql was NOT found on the classpath. " +
                        "Expected it at src/main/resources/external/schema.sql"
        );
    }

    @Test
    void productionDbMustBeOnClasspath() {
        URL resource = DBInitializer.class.getResource("/external/production.db");

        assertNotNull(
                resource,
                "production.db was NOT found on the classpath"
        );
    }
}
