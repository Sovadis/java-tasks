package ua.kpi.java.task4.vehicles;

import ua.kpi.java.task4.people.Human;

/**
 * Автомобіль — проміжний рівень ієрархії з рис. 1 (таксі, поліцейська та
 * пожежна машини). Власної логіки не додає: параметр типу {@code T} просто
 * передається далі до {@link Vehicle}, а конкретні підкласи його фіксують.
 *
 * @param <T> тип пасажирів, яких може перевозити автомобіль
 */
public abstract class Car<T extends Human> extends Vehicle<T> {

    /**
     * Створює автомобіль із заданою кількістю пасажирських місць.
     *
     * @param maxSeats кількість місць
     */
    protected Car(int maxSeats) {
        super(maxSeats);
    }
}
