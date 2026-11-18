package ua.kpi.java.task11.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.kpi.java.task11.DatabaseTest;
import ua.kpi.java.task11.model.Department;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести запитів до таблиці departments.
 */
class DepartmentDaoTest extends DatabaseTest {

    private DepartmentDao dao;

    @BeforeEach
    void createDao() {
        dao = new DepartmentDao(connection);
    }

    @Test
    void findAllReturnsDepartmentsOrderedById() throws Exception {
        List<Department> all = dao.findAll();

        assertEquals(4, all.size());
        assertEquals(List.of(10, 20, 30, 40), all.stream().map(Department::id).toList());
    }

    @Test
    void charValuesComeWithoutPadding() throws Exception {
        Department accounting = dao.findById(10).orElseThrow();

        // CHAR(30) у базі доповнюється пробілами, DAO їх відкидає
        assertEquals(new Department(10, "Бухгалтерія", "044-234-56-10"), accounting);
    }

    @Test
    void optionalPhoneIsNull() throws Exception {
        assertNull(dao.findById(40).orElseThrow().phone());
    }

    @Test
    void unknownDepartmentIsNotFound() throws Exception {
        assertTrue(dao.findById(99).isEmpty());
    }

    @Test
    void departmentWithEmployeesCannotBeDeleted() throws Exception {
        // зовнішній ключ departments_employees_fk без каскаду: відділ із співробітниками видалити не можна
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM departments WHERE department_id = ?")) {
            ps.setInt(1, 10);
            SQLException e = assertThrows(SQLException.class, ps::executeUpdate);
            assertEquals("23503", e.getSQLState());   // referential integrity violation
        }
        assertEquals(4, dao.findAll().size());
    }
}
