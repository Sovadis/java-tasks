package ua.kpi.java.task11.dao;

import ua.kpi.java.task11.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Запити до таблиці {@code tasks}.
 */
public class TaskDao {

    /** Максимальна довжина опису — як у стовпці {@code description VARCHAR(50)}. */
    public static final int MAX_DESCRIPTION_LENGTH = 50;

    private static final String SELECT = "SELECT task_id, description, employee_id FROM tasks";

    private final Connection connection;

    /** @param connection відкрите з'єднання з базою (його закриває той, хто створив) */
    public TaskDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return усі завдання, впорядковані за номером
     * @throws SQLException якщо запит не виконано
     */
    public List<Task> findAll() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT + " ORDER BY task_id")) {
            return list(ps);
        }
    }

    /**
     * @param employeeId номер співробітника
     * @return завдання співробітника (порожній список, якщо завдань немає)
     * @throws SQLException якщо запит не виконано
     */
    public List<Task> findByEmployee(int employeeId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT + " WHERE employee_id = ? ORDER BY task_id")) {
            ps.setInt(1, employeeId);
            return list(ps);
        }
    }

    /**
     * Додає завдання співробітнику. Номер завдання генерує СУБД.
     *
     * @param employeeId  номер співробітника
     * @param description опис (не довше {@value #MAX_DESCRIPTION_LENGTH} символів)
     * @return додане завдання разом зі згенерованим номером
     * @throws SQLException якщо співробітника не існує (порушено зовнішній ключ)
     *                      або опис задовгий (порушено обмеження VARCHAR(50))
     */
    public Task add(int employeeId, String description) throws SQLException {
        String sql = "INSERT INTO tasks (description, employee_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, description);
            ps.setInt(2, employeeId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Task(keys.getInt(1), description, employeeId);
            }
        }
    }

    /** Виконує підготовлений запит і перетворює всі рядки результату на об'єкти Task. */
    private static List<Task> list(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<Task> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new Task(rs.getInt("task_id"), rs.getString("description"), rs.getInt("employee_id")));
            }
            return result;
        }
    }
}
