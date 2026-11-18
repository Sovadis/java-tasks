package ua.kpi.java.task11.dao;

import ua.kpi.java.task11.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Запити до таблиці {@code departments}.
 */
public class DepartmentDao {

    private static final String SELECT = "SELECT department_id, name, phone FROM departments";

    private final Connection connection;

    /** @param connection відкрите з'єднання з базою (його закриває той, хто створив) */
    public DepartmentDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return усі відділи, впорядковані за номером
     * @throws SQLException якщо запит не виконано
     */
    public List<Department> findAll() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT + " ORDER BY department_id");
             ResultSet rs = ps.executeQuery()) {
            List<Department> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        }
    }

    /**
     * @param id номер відділу
     * @return відділ або порожній Optional, якщо такого номера немає
     * @throws SQLException якщо запит не виконано
     */
    public Optional<Department> findById(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT + " WHERE department_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    /** Перетворює поточний рядок результату на об'єкт Department. */
    private static Department map(ResultSet rs) throws SQLException {
        return new Department(rs.getInt("department_id"),
                trimChar(rs.getString("name")),
                trimChar(rs.getString("phone")));
    }

    /**
     * Тип CHAR(n) має фіксовану довжину: H2 доповнює значення пробілами до n символів,
     * тому пробіли в кінці відкидаємо. {@code null} (необов'язкове поле) лишається {@code null}.
     */
    static String trimChar(String value) {
        return value == null ? null : value.stripTrailing();
    }
}
