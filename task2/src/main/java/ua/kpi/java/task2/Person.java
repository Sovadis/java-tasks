package ua.kpi.java.task2;

import java.util.Objects;

/**
 * Особа з прізвищем, ім'ям та віком.
 * <p>
 * Клас незмінний: він оголошений як {@code final}, а всі поля — {@code private final}.
 * Методи {@link #equals(Object)} та {@link #hashCode()} написані вручну і узгоджені
 * між собою: рівні об'єкти завжди мають однаковий хеш-код.
 */
public final class Person {

    private final String lastName;
    private final String firstName;
    private final int age;

    /**
     * Створює особу.
     *
     * @param lastName  прізвище
     * @param firstName ім'я
     * @param age       вік (не може бути від'ємним)
     * @throws IllegalArgumentException якщо вік від'ємний
     */
    public Person(String lastName, String firstName, int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Вік не може бути від'ємним: " + age);
        }
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
    }

    /**
     * Повертає прізвище.
     *
     * @return прізвище
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Повертає ім'я.
     *
     * @return ім'я
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Повертає вік.
     *
     * @return вік
     */
    public int getAge() {
        return age;
    }

    /**
     * Порівнює цю особу з іншим об'єктом.
     * Дві особи рівні, якщо в них однакові прізвище, ім'я та вік.
     *
     * @param o об'єкт для порівняння
     * @return {@code true}, якщо {@code o} — теж {@code Person} з такими самими полями
     */
    @Override
    public boolean equals(Object o) {
        // 1. Це той самий об'єкт у пам'яті — отже, рівні.
        if (this == o) {
            return true;
        }
        // 2. null або об'єкт іншого класу — не рівні.
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        // 3. Порівнюємо всі поля. Objects.equals коректно обробляє null.
        Person other = (Person) o;
        return age == other.age
                && Objects.equals(lastName, other.lastName)
                && Objects.equals(firstName, other.firstName);
    }

    /**
     * Обчислює хеш-код з тих самих полів, що беруть участь у {@link #equals(Object)}.
     *
     * @return хеш-код особи
     */
    @Override
    public int hashCode() {
        return Objects.hash(lastName, firstName, age);
    }

    /**
     * Повертає текстове представлення особи.
     *
     * @return рядок з усіма полями
     */
    @Override
    public String toString() {
        return "Person{lastName='" + lastName + "', firstName='" + firstName + "', age=" + age + "}";
    }
}
