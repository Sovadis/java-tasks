package ua.kpi.java.task4.vehicles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ua.kpi.java.task4.exceptions.NoFreeSeatsException;
import ua.kpi.java.task4.exceptions.PassengerNotFoundException;
import ua.kpi.java.task4.people.Human;

/**
 * Транспортний засіб з обмеженою кількістю пасажирських місць.
 *
 * <p>Параметр типу {@code T} визначає, яких пасажирів може перевозити транспорт.
 * Межа {@code T extends Human} гарантує, що пасажиром може бути лише людина,
 * а конкретні підкласи фіксують тип пасажира: {@code Bus extends Vehicle<Human>},
 * {@code FireTruck extends Car<Firefighter>} тощо. Методи {@link #board} і
 * {@link #disembark} приймають саме {@code T}, тому спроба посадити, наприклад,
 * поліцейського в пожежну машину — це помилка компіляції, а не помилка під час
 * виконання програми.
 *
 * @param <T> тип пасажирів, яких може перевозити транспорт
 */
public abstract class Vehicle<T extends Human> {

    private final int maxSeats;
    private final List<T> passengers = new ArrayList<>();

    /**
     * Створює порожній транспорт із заданою кількістю місць.
     *
     * @param maxSeats максимальна кількість пасажирських місць (більше нуля)
     * @throws IllegalArgumentException якщо кількість місць не додатна
     */
    protected Vehicle(int maxSeats) {
        if (maxSeats <= 0) {
            throw new IllegalArgumentException("Кількість місць має бути більшою за нуль: " + maxSeats);
        }
        this.maxSeats = maxSeats;
    }

    /**
     * Повертає назву типу транспорту українською, наприклад «Автобус».
     *
     * @return назва типу транспорту
     */
    public abstract String getTypeName();

    /**
     * Повертає максимальну кількість пасажирських місць.
     *
     * @return кількість місць
     */
    public int getMaxSeats() {
        return maxSeats;
    }

    /**
     * Повертає кількість зайнятих місць, тобто кількість пасажирів у транспорті.
     *
     * @return кількість зайнятих місць
     */
    public int getOccupiedSeats() {
        return passengers.size();
    }

    /**
     * Садить пасажира в транспорт.
     *
     * @param passenger пасажир допустимого для цього транспорту типу
     * @throws NoFreeSeatsException якщо всі місця вже зайнято
     * @throws IllegalArgumentException якщо цей пасажир уже їде в транспорті
     * @throws NullPointerException якщо {@code passenger} дорівнює {@code null}
     */
    public void board(T passenger) throws NoFreeSeatsException {
        Objects.requireNonNull(passenger, "Пасажир не може бути null");
        if (passengers.contains(passenger)) {
            throw new IllegalArgumentException(passenger + " уже їде в транспорті «" + getTypeName() + "»");
        }
        if (passengers.size() >= maxSeats) {
            throw new NoFreeSeatsException("У транспорті «" + getTypeName()
                    + "» немає вільних місць (усього місць: " + maxSeats + ")");
        }
        passengers.add(passenger);
    }

    /**
     * Садить у транспорт одразу кількох пасажирів.
     *
     * <p>Wildcard {@code Collection<? extends T>} дозволяє передати колекцію
     * будь-якого підтипу {@code T}: наприклад, в автобус ({@code Vehicle<Human>})
     * можна посадити {@code List<Firefighter>}. Без wildcard метод приймав би лише
     * {@code Collection<Human>}, бо generics інваріантні. Колекцію ми тільки читаємо
     * (вона «постачає» пасажирів), тому використано саме {@code extends}.
     *
     * <p>Вільні місця перевіряються заздалегідь: якщо їх не вистачає на всіх,
     * не сідає жоден пасажир.
     *
     * @param newPassengers пасажири, яких треба посадити
     * @throws NoFreeSeatsException якщо вільних місць менше, ніж пасажирів
     */
    public void boardAll(Collection<? extends T> newPassengers) throws NoFreeSeatsException {
        int freeSeats = maxSeats - passengers.size();
        if (newPassengers.size() > freeSeats) {
            throw new NoFreeSeatsException("У транспорті «" + getTypeName()
                    + "» недостатньо вільних місць: потрібно " + newPassengers.size()
                    + ", вільно " + freeSeats);
        }
        for (T passenger : newPassengers) {
            board(passenger);
        }
    }

    /**
     * Висаджує пасажира з транспорту.
     *
     * @param passenger пасажир, якого треба висадити
     * @throws PassengerNotFoundException якщо цього пасажира немає в транспорті
     */
    public void disembark(T passenger) throws PassengerNotFoundException {
        if (!passengers.remove(passenger)) {
            throw new PassengerNotFoundException(passenger + " не їде в транспорті «" + getTypeName() + "»");
        }
    }

    /**
     * Повертає незмінну копію списку пасажирів у порядку посадки.
     *
     * @return пасажири транспорту
     */
    public List<T> getPassengers() {
        return List.copyOf(passengers);
    }

    /**
     * Повертає тип транспорту та заповненість, наприклад «Автобус (зайнято 2 з 3 місць)».
     *
     * @return текстове подання транспорту
     */
    @Override
    public String toString() {
        return getTypeName() + " (зайнято " + getOccupiedSeats() + " з " + maxSeats + " місць)";
    }
}
