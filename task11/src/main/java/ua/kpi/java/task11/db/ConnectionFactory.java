package ua.kpi.java.task11.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Створює з'єднання з базою даних за параметрами з файлу властивостей.
 * <p>
 * Файл містить ключі {@code db.url}, {@code db.user} і {@code db.password}. Драйвер окремо не
 * завантажується: {@link DriverManager} сам знаходить JDBC-драйвер, що лежить у classpath
 * (H2 або, наприклад, MySQL), за початком URL.
 */
public class ConnectionFactory {

    /** Файл властивостей програми (у ресурсах). */
    public static final String DEFAULT_PROPERTIES = "/db.properties";

    private final String url;
    private final String user;
    private final String password;

    /**
     * @param properties параметри з'єднання; {@code db.url} обов'язковий
     * @throws IllegalArgumentException якщо не задано {@code db.url}
     */
    public ConnectionFactory(Properties properties) {
        String value = properties.getProperty("db.url", "").trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("у файлі властивостей не задано db.url");
        }
        this.url = value;
        this.user = properties.getProperty("db.user", "").trim();
        this.password = properties.getProperty("db.password", "");
    }

    /**
     * Читає параметри з файлу властивостей, що лежить у ресурсах програми.
     *
     * @param resource ім'я ресурсу, наприклад {@code /db.properties}
     * @return фабрика з'єднань
     * @throws IOException якщо файл не знайдено або не вдалося прочитати
     */
    public static ConnectionFactory fromResource(String resource) throws IOException {
        try (InputStream in = ConnectionFactory.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("не знайдено файл властивостей " + resource);
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return new ConnectionFactory(properties);
        }
    }

    /**
     * Відкриває нове з'єднання. Закривати його має той, хто викликав метод.
     *
     * @return відкрите з'єднання
     * @throws SQLException якщо з'єднатися не вдалося
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** @return URL бази даних з файлу властивостей */
    public String getUrl() {
        return url;
    }

    /** @return ім'я користувача бази даних */
    public String getUser() {
        return user;
    }
}
