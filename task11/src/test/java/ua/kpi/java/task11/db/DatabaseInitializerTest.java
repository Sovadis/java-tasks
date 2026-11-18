package ua.kpi.java.task11.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести створення бази зі скриптів schema.sql і data.sql.
 */
class DatabaseInitializerTest {

    private static Connection open(String url) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("db.url", url);
        properties.setProperty("db.user", "sa");
        return new ConnectionFactory(properties).getConnection();
    }

    /** Кількість рядків таблиці (ім'я таблиці — константа тесту, не введення користувача). */
    private static int count(Connection connection, String table) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    void scriptsAreSplitIntoStatements() throws Exception {
        List<String> schema = DatabaseInitializer.readStatements(DatabaseInitializer.SCHEMA_SCRIPT);
        List<String> data = DatabaseInitializer.readStatements(DatabaseInitializer.DATA_SCRIPT);

        assertEquals(6, schema.size());   // 3 × DROP TABLE + 3 × CREATE TABLE
        assertEquals(3, data.size());     // по одному INSERT на таблицю
        assertTrue(schema.get(3).startsWith("CREATE TABLE departments"));
    }

    @Test
    void emptyDatabaseIsCreatedAndFilled() throws Exception {
        try (Connection connection = open("jdbc:h2:mem:init-empty")) {
            assertFalse(DatabaseInitializer.isInitialized(connection));

            DatabaseInitializer.recreate(connection);

            assertTrue(DatabaseInitializer.isInitialized(connection));
            assertEquals(4, count(connection, "departments"));
            assertEquals(11, count(connection, "employees"));
            assertEquals(14, count(connection, "tasks"));
        }
    }

    @Test
    void recreateRestoresInitialData() throws Exception {
        try (Connection connection = open("jdbc:h2:mem:init-restore")) {
            DatabaseInitializer.recreate(connection);
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tasks (description, employee_id) VALUES ('Зайве завдання', 101)")) {
                ps.executeUpdate();
            }
            assertEquals(15, count(connection, "tasks"));

            DatabaseInitializer.recreate(connection);

            assertEquals(11, count(connection, "employees"));
            assertEquals(14, count(connection, "tasks"));
        }
    }

    @Test
    void scriptsRunInMySqlCompatibilityMode() throws Exception {
        try (Connection connection = open("jdbc:h2:mem:init-mysql;MODE=MySQL")) {
            DatabaseInitializer.recreate(connection);

            assertEquals(14, count(connection, "tasks"));
        }
    }
}
