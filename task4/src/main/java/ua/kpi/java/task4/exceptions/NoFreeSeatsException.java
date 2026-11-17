package ua.kpi.java.task4.exceptions;

/**
 * Виникає при спробі посадити пасажира в транспорт, у якому зайнято всі місця.
 *
 * <p>Виняток перевірюваний (checked): заповнений транспорт — це звичайна
 * ситуація, з якої програма може відновитися (наприклад, посадити пасажира
 * в інший транспорт), тому компілятор змушує код, що викликає посадку,
 * явно її обробити.
 */
public class NoFreeSeatsException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Створює виняток із повідомленням.
     *
     * @param message опис ситуації
     */
    public NoFreeSeatsException(String message) {
        super(message);
    }
}
