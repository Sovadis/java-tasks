package ua.kpi.java.task11.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.kpi.java.task11.DatabaseTest;
import ua.kpi.java.task11.model.Task;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести запитів до таблиці tasks: вибірка, додавання та обмеження схеми.
 */
class TaskDaoTest extends DatabaseTest {

    private TaskDao tasks;

    @BeforeEach
    void createDao() {
        tasks = new TaskDao(connection);
    }

    @Test
    void findAllReturnsAllTasksOrderedById() throws Exception {
        List<Task> all = tasks.findAll();

        assertEquals(14, all.size());
        assertEquals(new Task(1, "Підготувати квартальний податковий звіт", 101), all.get(0));
        assertEquals("Провести код-рев'ю модуля замовлень", all.get(3).description());
    }

    @Test
    void findByEmployeeReturnsOnlyTheirTasks() throws Exception {
        assertEquals(List.of(2, 3), tasks.findByEmployee(102).stream().map(Task::id).toList());
        assertTrue(tasks.findByEmployee(999).isEmpty());
    }

    @Test
    void addTaskGetsGeneratedIdAndIsStored() throws Exception {
        Task added = tasks.add(106, "Підготувати демо для замовника");

        assertEquals(15, added.id());
        assertEquals(List.of(9, 15), tasks.findByEmployee(106).stream().map(Task::id).toList());
        assertEquals(added, tasks.findAll().get(14));
    }

    @Test
    void descriptionLongerThan50IsRejected() throws Exception {
        String tooLong = "О".repeat(TaskDao.MAX_DESCRIPTION_LENGTH + 1);

        SQLException e = assertThrows(SQLException.class, () -> tasks.add(101, tooLong));
        assertEquals("22001", e.getSQLState());   // value too long
        assertEquals(14, tasks.findAll().size());
        // рівно 50 символів — допустимо
        tasks.add(101, "О".repeat(TaskDao.MAX_DESCRIPTION_LENGTH));
    }

    @Test
    void taskForUnknownEmployeeIsRejectedByForeignKey() throws Exception {
        SQLException e = assertThrows(SQLException.class, () -> tasks.add(999, "Завдання без виконавця"));
        assertEquals("23506", e.getSQLState());   // referential integrity violation
        assertEquals(14, tasks.findAll().size());
    }

    @Test
    void parametersAreNotInterpretedAsSql() throws Exception {
        String injection = "x'); DROP TABLE tasks; --";

        Task added = tasks.add(101, injection);

        assertEquals(injection, tasks.findAll().get(14).description());
        assertEquals(added.id(), tasks.findAll().get(14).id());
    }
}
