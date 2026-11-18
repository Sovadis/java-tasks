package ua.kpi.java.task11.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Створення бази даних зі SQL-скриптів {@code schema.sql} (таблиці) і {@code data.sql} (дані).
 * <p>
 * Ті самі скрипти можна виконати вручну у веб-консолі H2.
 */
public final class DatabaseInitializer {

    /** Скрипт створення таблиць (у ресурсах). */
    public static final String SCHEMA_SCRIPT = "/schema.sql";
    /** Скрипт заповнення таблиць початковими даними (у ресурсах). */
    public static final String DATA_SCRIPT = "/data.sql";

    private static final String[] TABLES = {"departments", "employees", "tasks"};

    private DatabaseInitializer() {
    }

    /**
     * Перевіряє, чи в базі вже є всі три таблиці.
     *
     * @param connection з'єднання з базою
     * @return {@code true}, якщо таблиці існують
     * @throws SQLException якщо не вдалося прочитати метадані
     */
    public static boolean isInitialized(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        int found = 0;
        // H2 зберігає імена без лапок великими літерами, MySQL — як записано, тому порівнюємо без регістру.
        try (ResultSet rs = meta.getTables(null, null, "%", null)) {
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                for (String table : TABLES) {
                    if (table.equalsIgnoreCase(name)) {
                        found++;
                    }
                }
            }
        }
        return found == TABLES.length;
    }

    /**
     * Видаляє і створює таблиці заново, а потім заповнює їх початковими даними.
     * Дані вставляються в одній транзакції: або всі рядки, або жодного.
     *
     * @param connection з'єднання з базою
     * @throws SQLException якщо помилка під час виконання SQL
     * @throws IOException  якщо скрипт не знайдено в ресурсах
     */
    public static void recreate(Connection connection) throws SQLException, IOException {
        List<String> schema = readStatements(SCHEMA_SCRIPT);
        List<String> data = readStatements(DATA_SCRIPT);

        // DDL (CREATE/DROP) і в H2, і в MySQL фіксується автоматично, тому окремо від даних.
        execute(connection, schema);

        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            execute(connection, data);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    /**
     * Читає SQL-скрипт з ресурсів і ділить його на окремі оператори за символом «;».
     * Рядки-коментарі ({@code --} на початку рядка) пропускаються.
     *
     * @param resource ім'я ресурсу, наприклад {@code /schema.sql}
     * @return оператори SQL без кінцевої «;»
     * @throws IOException якщо ресурс не знайдено
     */
    static List<String> readStatements(String resource) throws IOException {
        String text;
        try (InputStream in = DatabaseInitializer.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("не знайдено скрипт " + resource);
            }
            text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : text.split("\\R")) {
            if (line.strip().startsWith("--")) {
                continue;
            }
            current.append(line).append('\n');
            if (line.strip().endsWith(";")) {
                String sql = current.toString().strip();
                statements.add(sql.substring(0, sql.length() - 1));
                current.setLength(0);
            }
        }
        if (!current.toString().isBlank()) {
            statements.add(current.toString().strip());
        }
        return statements;
    }

    /** Виконує оператори скрипту по черзі (скрипт не містить параметрів, тому достатньо Statement). */
    private static void execute(Connection connection, List<String> statements) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }
}
