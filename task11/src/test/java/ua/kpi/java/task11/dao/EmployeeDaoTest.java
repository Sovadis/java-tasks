package ua.kpi.java.task11.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.kpi.java.task11.DatabaseTest;
import ua.kpi.java.task11.model.Employee;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести запитів до таблиці employees, зокрема каскадного видалення завдань.
 */
class EmployeeDaoTest extends DatabaseTest {

    private EmployeeDao employees;
    private TaskDao tasks;

    @BeforeEach
    void createDao() {
        employees = new EmployeeDao(connection);
        tasks = new TaskDao(connection);
    }

    @Test
    void findAllReturnsAllEmployeesOrderedById() throws Exception {
        List<Employee> all = employees.findAll();

        assertEquals(11, all.size());
        assertEquals(new Employee(101, "Шевченко", "Олена", "Головний бухгалтер", 10), all.get(0));
        assertEquals(111, all.get(10).id());
    }

    @Test
    void findByDepartmentReturnsOnlyItsEmployees() throws Exception {
        List<Employee> testers = employees.findByDepartment(30);

        assertEquals(List.of(107, 108, 109), testers.stream().map(Employee::id).toList());
        assertTrue(testers.stream().allMatch(e -> e.departmentId() == 30));
    }

    @Test
    void unknownDepartmentGivesEmptyResult() throws Exception {
        assertTrue(employees.findByDepartment(99).isEmpty());
    }

    @Test
    void findByIdAndOptionalPosition() throws Exception {
        Employee intern = employees.findById(106).orElseThrow();

        assertEquals("Мельник Дмитро", intern.fullName());
        assertNull(intern.position());
        assertTrue(employees.findById(999).isEmpty());
    }

    @Test
    void deleteEmployeeRemovesTheirTasks() throws Exception {
        assertEquals(2, tasks.findByEmployee(105).size());

        assertTrue(employees.delete(105));

        assertTrue(employees.findById(105).isEmpty());
        assertTrue(tasks.findByEmployee(105).isEmpty());
        assertEquals(10, employees.findAll().size());
        assertEquals(12, tasks.findAll().size());   // 14 − 2 завдання видаленого співробітника
    }

    @Test
    void deleteUnknownEmployeeChangesNothing() throws Exception {
        assertFalse(employees.delete(999));

        assertEquals(11, employees.findAll().size());
        assertEquals(14, tasks.findAll().size());
    }
}
