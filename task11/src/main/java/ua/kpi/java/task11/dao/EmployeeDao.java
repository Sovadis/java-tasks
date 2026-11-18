package ua.kpi.java.task11.dao;

import ua.kpi.java.task11.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Запити до таблиці {@code employees}.
 */
public class EmployeeDao {

    private static final String SELECT =
            "SELECT employee_id, last_name, first_name, position, department_id FROM employees";

    private final Connection connection;

    /** @param connection відкрите з'єднання з базою (його закриває той, хто створив) */
    public EmployeeDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return усі співробітники, впорядковані за номером
     * @throws SQLException якщо запит не виконано
     */
    public List<Employee> findAll() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT + " ORDER BY employee_id")) {
            return list(ps);
        }
    }

    /**
     * @param departmentId номер відділу
     * @return співробітники відділу (порожній список, якщо відділу немає або в ньому нікого немає)
     * @throws SQLException якщо запит не виконано
     */
    public List<Employee> findByDepartment(int departmentId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                SELECT + " WHERE department_id = ? ORDER BY employee_id")) {
            ps.setInt(1, departmentId);
            return list(ps);
        }
    }

    /**
     * @param id номер співробітника
     * @return співробітник або порожній Optional, якщо такого номера немає
     * @throws SQLException якщо запит не виконано
     */
    public Optional<Employee> findById(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT + " WHERE employee_id = ?")) {
            ps.setInt(1, id);
            List<Employee> found = list(ps);
            return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
        }
    }

    /**
     * Видаляє співробітника. Його завдання видаляє сама СУБД за правилом
     * {@code ON DELETE CASCADE} зовнішнього ключа {@code employees_tasks_fk}.
     *
     * @param id номер співробітника
     * @return {@code true}, якщо співробітника видалено; {@code false}, якщо такого номера немає
     * @throws SQLException якщо запит не виконано
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /** Виконує підготовлений запит і перетворює всі рядки результату на об'єкти Employee. */
    private static List<Employee> list(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<Employee> result = new ArrayList<>();
            while (rs.next()) {
                result.add(new Employee(rs.getInt("employee_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("position"),
                        rs.getInt("department_id")));
            }
            return result;
        }
    }
}
