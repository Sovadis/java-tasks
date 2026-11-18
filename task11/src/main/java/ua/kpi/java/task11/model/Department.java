package ua.kpi.java.task11.model;

/**
 * Відділ — рядок таблиці {@code departments}.
 *
 * @param id    номер відділу (первинний ключ)
 * @param name  назва
 * @param phone телефон або {@code null}, якщо його не вказано
 */
public record Department(int id, String name, String phone) {
}
