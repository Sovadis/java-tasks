package ua.kpi.java.task4.people;

/**
 * Людина — базовий клас для всіх видів пасажирів (рис. 1).
 *
 * <p>Клас абстрактний: у моделі кожна людина має конкретну роль —
 * {@link Passenger} (звичайний пасажир), {@link Firefighter} (пожежник)
 * або {@link Policeman} (поліцейський).
 *
 * <p>Методи {@code equals}/{@code hashCode} навмисно не перевизначено, тобто
 * люди порівнюються за ідентичністю об'єкта (за посиланням). Імена можуть
 * повторюватися: два об'єкти з іменем «Іван» — це різні люди-тезки, тому
 * висадити з транспорту можна лише того, хто справді в нього сів.
 */
public abstract class Human {

    private final String name;

    /**
     * Створює людину з указаним іменем.
     *
     * @param name ім'я (не порожнє)
     * @throws IllegalArgumentException якщо ім'я {@code null} або порожнє
     */
    protected Human(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ім'я людини не може бути порожнім");
        }
        this.name = name;
    }

    /**
     * Повертає ім'я людини.
     *
     * @return ім'я
     */
    public String getName() {
        return name;
    }

    /**
     * Повертає назву ролі людини українською, наприклад «Пожежник».
     *
     * @return назва ролі
     */
    public abstract String getRole();

    /**
     * Повертає роль та ім'я, наприклад «Пожежник Петро».
     *
     * @return текстове подання людини
     */
    @Override
    public String toString() {
        return getRole() + " " + name;
    }
}
