package ua.kpi.java.task11;

import ua.kpi.java.task11.dao.DepartmentDao;
import ua.kpi.java.task11.dao.EmployeeDao;
import ua.kpi.java.task11.dao.TaskDao;
import ua.kpi.java.task11.db.ConnectionFactory;
import ua.kpi.java.task11.db.DatabaseInitializer;
import ua.kpi.java.task11.model.Department;
import ua.kpi.java.task11.model.Employee;
import ua.kpi.java.task11.model.Task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Консольна програма для роботи з базою даних «Відділи — Співробітники — Завдання» (завдання 11).
 * <p>
 * Програма з'єднується з базою за параметрами з {@code db.properties}, за першого запуску створює
 * таблиці зі скриптів {@code schema.sql} і {@code data.sql}, а далі через меню виконує запити.
 * <p>
 * Якщо запустити з аргументом {@code --echo}, програма повторює кожен введений рядок у виводі —
 * це зручно, коли вхідні дані подаються з файлу.
 */
public class Main {

    private static ConsoleInput input;
    private static DepartmentDao departmentDao;
    private static EmployeeDao employeeDao;
    private static TaskDao taskDao;

    /**
     * Точка входу програми.
     *
     * @param args {@code --echo} — повторювати введені рядки у виводі
     */
    public static void main(String[] args) {
        input = new ConsoleInput(System.in, args.length > 0 && "--echo".equals(args[0]));
        System.out.println("=== Робота з базою даних: відділи, співробітники, завдання ===");

        ConnectionFactory factory;
        try {
            factory = ConnectionFactory.fromResource(ConnectionFactory.DEFAULT_PROPERTIES);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
            return;
        }

        System.out.println("Підключення до бази даних " + factory.getUrl() + " ...");
        try (Connection connection = factory.getConnection()) {
            System.out.println("З'єднання встановлено: " + connection.getMetaData().getDatabaseProductName()
                    + " " + connection.getMetaData().getDatabaseProductVersion());
            departmentDao = new DepartmentDao(connection);
            employeeDao = new EmployeeDao(connection);
            taskDao = new TaskDao(connection);
            if (!DatabaseInitializer.isInitialized(connection)) {
                System.out.println("Таблиць ще немає — створюю базу зі скриптів schema.sql і data.sql.");
                DatabaseInitializer.recreate(connection);
                printCounts();
            }
            runMenu(connection);
        } catch (NoSuchElementException e) {
            // Введення закінчилося (наприклад, файл з даними прочитано повністю).
            System.out.println();
            System.out.println("Введення завершено. До побачення!");
        } catch (SQLException | IOException e) {
            System.out.println("Помилка бази даних: " + e.getMessage());
        }
    }

    /** Показує меню і виконує обрані дії, доки користувач не обере вихід. */
    private static void runMenu(Connection connection) throws IOException {
        while (true) {
            System.out.println();
            System.out.println("========== МЕНЮ ==========");
            System.out.println("  1 - Усі співробітники");
            System.out.println("  2 - Усі завдання");
            System.out.println("  3 - Співробітники відділу");
            System.out.println("  4 - Додати завдання співробітнику");
            System.out.println("  5 - Завдання співробітника");
            System.out.println("  6 - Видалити співробітника");
            System.out.println("  7 - Перестворити базу даних зі скриптів");
            System.out.println("  0 - Вихід");
            String choice = input.readLine("Ваш вибір: ");
            try {
                switch (choice) {
                    case "1" -> showAllEmployees();
                    case "2" -> showAllTasks();
                    case "3" -> showDepartmentEmployees();
                    case "4" -> addTask();
                    case "5" -> showEmployeeTasks();
                    case "6" -> deleteEmployee();
                    case "7" -> recreateDatabase(connection);
                    case "0" -> {
                        System.out.println("З'єднання з базою даних закрито. До побачення!");
                        return;
                    }
                    default -> System.out.println("Помилка: немає пункту меню «" + choice + "». Оберіть від 0 до 7.");
                }
            } catch (SQLException e) {
                System.out.println("Помилка бази даних: " + e.getMessage());
            }
        }
    }

    /** Пункт 1: таблиця всіх співробітників з назвами відділів. */
    private static void showAllEmployees() throws SQLException {
        System.out.println("--- Усі співробітники ---");
        printEmployees(employeeDao.findAll());
    }

    /** Пункт 2: таблиця всіх завдань з прізвищами виконавців. */
    private static void showAllTasks() throws SQLException {
        System.out.println("--- Усі завдання ---");
        Map<Integer, String> names = new HashMap<>();
        for (Employee e : employeeDao.findAll()) {
            names.put(e.id(), e.fullName());
        }
        TablePrinter table = new TablePrinter("№", "Опис завдання", "№ співр.", "Співробітник");
        for (Task t : taskDao.findAll()) {
            table.addRow(t.id(), t.description(), t.employeeId(), names.get(t.employeeId()));
        }
        table.print();
    }

    /** Пункт 3: співробітники відділу, номер якого вводиться з клавіатури. */
    private static void showDepartmentEmployees() throws SQLException {
        System.out.println("--- Співробітники відділу ---");
        TablePrinter table = new TablePrinter("№", "Назва відділу", "Телефон");
        for (Department d : departmentDao.findAll()) {
            table.addRow(d.id(), d.name(), d.phone());
        }
        table.print();
        Department department = readDepartment();
        System.out.println("Відділ №" + department.id() + " «" + department.name() + "», телефон: "
                + (department.phone() == null ? "не вказано" : department.phone()));
        List<Employee> employees = employeeDao.findByDepartment(department.id());
        if (employees.isEmpty()) {
            System.out.println("У відділі немає співробітників.");
        } else {
            printEmployees(employees);
        }
    }

    /** Пункт 4: нове завдання для співробітника. */
    private static void addTask() throws SQLException {
        System.out.println("--- Додавання завдання ---");
        Employee employee = readEmployee();
        System.out.println("Співробітник: " + employee.fullName());
        String description = input.readText("Опис завдання (до " + TaskDao.MAX_DESCRIPTION_LENGTH + " символів): ",
                TaskDao.MAX_DESCRIPTION_LENGTH);
        Task task = taskDao.add(employee.id(), description);
        System.out.println("Додано завдання №" + task.id() + " для співробітника " + employee.fullName() + ".");
    }

    /** Пункт 5: завдання співробітника, номер якого вводиться з клавіатури. */
    private static void showEmployeeTasks() throws SQLException {
        System.out.println("--- Завдання співробітника ---");
        Employee employee = readEmployee();
        List<Task> tasks = taskDao.findByEmployee(employee.id());
        System.out.println("Співробітник №" + employee.id() + " " + employee.fullName() + ", завдань: " + tasks.size());
        if (!tasks.isEmpty()) {
            TablePrinter table = new TablePrinter("№", "Опис завдання");
            for (Task t : tasks) {
                table.addRow(t.id(), t.description());
            }
            table.print();
        }
    }

    /** Пункт 6: видалення співробітника разом з його завданнями (після підтвердження). */
    private static void deleteEmployee() throws SQLException {
        System.out.println("--- Видалення співробітника ---");
        Employee employee = readEmployee();
        int taskCount = taskDao.findByEmployee(employee.id()).size();
        System.out.println("Буде видалено співробітника №" + employee.id() + " " + employee.fullName()
                + " і його завдання (" + taskCount + ").");
        if (!input.confirm("Підтвердіть видалення (так/ні): ")) {
            System.out.println("Видалення скасовано.");
            return;
        }
        if (employeeDao.delete(employee.id())) {
            System.out.println("Співробітника №" + employee.id() + " видалено.");
        } else {
            System.out.println("Співробітника №" + employee.id() + " вже немає в базі.");
        }
    }

    /** Пункт 7: повторне виконання schema.sql і data.sql — повернення початкових даних. */
    private static void recreateDatabase(Connection connection) throws SQLException, IOException {
        System.out.println("--- Перестворення бази даних ---");
        if (!input.confirm("Усі зміни буде втрачено, дані стануть початковими. Продовжити (так/ні): ")) {
            System.out.println("Перестворення скасовано.");
            return;
        }
        DatabaseInitializer.recreate(connection);
        printCounts();
    }

    /** Виводить кількість рядків у кожній таблиці після створення бази. */
    private static void printCounts() throws SQLException {
        System.out.println("Базу даних створено: відділів — " + departmentDao.findAll().size()
                + ", співробітників — " + employeeDao.findAll().size()
                + ", завдань — " + taskDao.findAll().size() + ".");
    }

    /** Виводить список співробітників таблицею. */
    private static void printEmployees(List<Employee> employees) throws SQLException {
        Map<Integer, String> departments = new HashMap<>();
        for (Department d : departmentDao.findAll()) {
            departments.put(d.id(), d.name());
        }
        TablePrinter table = new TablePrinter("№", "Прізвище", "Ім'я", "Посада", "Відділ");
        for (Employee e : employees) {
            table.addRow(e.id(), e.lastName(), e.firstName(), e.position(),
                    e.departmentId() + " " + departments.get(e.departmentId()));
        }
        table.print();
    }

    /** Читає номер відділу, доки не буде введено номер наявного відділу. */
    private static Department readDepartment() throws SQLException {
        while (true) {
            int id = input.readInt("Номер відділу: ");
            Optional<Department> department = departmentDao.findById(id);
            if (department.isPresent()) {
                return department.get();
            }
            System.out.println("Помилка: відділу з номером " + id + " немає. Спробуйте ще раз.");
        }
    }

    /** Читає номер співробітника, доки не буде введено номер наявного співробітника. */
    private static Employee readEmployee() throws SQLException {
        while (true) {
            int id = input.readInt("Номер співробітника: ");
            Optional<Employee> employee = employeeDao.findById(id);
            if (employee.isPresent()) {
                return employee.get();
            }
            System.out.println("Помилка: співробітника з номером " + id + " немає. Спробуйте ще раз.");
        }
    }
}
