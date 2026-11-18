package ua.kpi.java.task11.model;

/**
 * Співробітник — рядок таблиці {@code employees}.
 *
 * @param id           номер співробітника (первинний ключ)
 * @param lastName     прізвище
 * @param firstName    ім'я
 * @param position     посада або {@code null}, якщо її не вказано
 * @param departmentId номер відділу (зовнішній ключ)
 */
public record Employee(int id, String lastName, String firstName, String position, int departmentId) {

    /** @return прізвище та ім'я, наприклад «Мельник Дмитро» */
    public String fullName() {
        return lastName + " " + firstName;
    }
}
