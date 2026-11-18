package ua.kpi.java.task11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ua.kpi.java.task11.db.ConnectionFactory;
import ua.kpi.java.task11.db.DatabaseInitializer;

import java.sql.Connection;

/**
 * Базовий клас тестів DAO: перед кожним тестом база в пам'яті H2 створюється заново
 * з тих самих скриптів schema.sql і data.sql, що й у програмі.
 */
public abstract class DatabaseTest {

    protected Connection connection;

    @BeforeEach
    void openAndRecreate() throws Exception {
        connection = ConnectionFactory.fromResource("/db-test.properties").getConnection();
        DatabaseInitializer.recreate(connection);
    }

    @AfterEach
    void close() throws Exception {
        connection.close();
    }
}
