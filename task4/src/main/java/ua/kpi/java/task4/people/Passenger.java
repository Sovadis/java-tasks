package ua.kpi.java.task4.people;

/**
 * Звичайний пасажир. Може їхати лише в транспорті для будь-яких людей
 * (автобус, таксі).
 *
 * <p>Цей клас додано до ієрархії з рис. 1, щоб звичайний пасажир був окремим
 * типом поряд із пожежником і поліцейським, а {@link Human} залишався
 * спільним абстрактним предком для всіх трьох.
 */
public class Passenger extends Human {

    /**
     * Створює звичайного пасажира.
     *
     * @param name ім'я пасажира
     */
    public Passenger(String name) {
        super(name);
    }

    /**
     * Повертає назву ролі.
     *
     * @return «Пасажир»
     */
    @Override
    public String getRole() {
        return "Пасажир";
    }
}
