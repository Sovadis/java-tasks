package ua.kpi.java.task4.exceptions;

/**
 * Виникає при спробі висадити пасажира, якого немає в транспорті.
 *
 * <p>Виняток перевірюваний (checked) — так само, як {@link NoFreeSeatsException}:
 * обидві ситуації передбачені умовою задачі, і код, що викликає висадку,
 * має їх явно обробити.
 */
public class PassengerNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Створює виняток із повідомленням.
     *
     * @param message опис ситуації
     */
    public PassengerNotFoundException(String message) {
        super(message);
    }
}
