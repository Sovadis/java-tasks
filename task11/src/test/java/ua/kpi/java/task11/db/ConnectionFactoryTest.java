package ua.kpi.java.task11.db;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тести читання параметрів з'єднання з файлу властивостей.
 */
class ConnectionFactoryTest {

    @Test
    void readsParametersFromPropertiesFile() throws Exception {
        ConnectionFactory factory = ConnectionFactory.fromResource("/db-test.properties");

        assertEquals("jdbc:h2:mem:task11test;DB_CLOSE_DELAY=-1", factory.getUrl());
        assertEquals("sa", factory.getUser());
    }

    @Test
    void programPropertiesPointToFileDatabase() throws Exception {
        ConnectionFactory factory = ConnectionFactory.fromResource(ConnectionFactory.DEFAULT_PROPERTIES);

        assertEquals("jdbc:h2:./data/company", factory.getUrl());
    }

    @Test
    void opensWorkingConnection() throws Exception {
        try (Connection connection = ConnectionFactory.fromResource("/db-test.properties").getConnection()) {
            assertFalse(connection.isClosed());
            assertEquals("H2", connection.getMetaData().getDatabaseProductName());
        }
    }

    @Test
    void missingFileIsReported() {
        assertThrows(IOException.class, () -> ConnectionFactory.fromResource("/no-such.properties"));
    }

    @Test
    void missingUrlIsRejected() {
        Properties properties = new Properties();
        properties.setProperty("db.user", "sa");

        assertThrows(IllegalArgumentException.class, () -> new ConnectionFactory(properties));
    }
}
