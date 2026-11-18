package ua.kpi.java.task11.model;

/**
 * Завдання — рядок таблиці {@code tasks}.
 *
 * @param id          номер завдання (первинний ключ, генерує СУБД)
 * @param description опис завдання
 * @param employeeId  номер співробітника (зовнішній ключ)
 */
public record Task(int id, String description, int employeeId) {
}
