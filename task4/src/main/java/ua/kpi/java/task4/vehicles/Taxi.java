package ua.kpi.java.task4.vehicles;

import ua.kpi.java.task4.people.Human;

/**
 * Таксі — автомобіль, що перевозить будь-яких людей, тому параметр типу
 * зафіксовано як {@link Human}.
 */
public class Taxi extends Car<Human> {

    /**
     * Створює таксі.
     *
     * @param maxSeats кількість пасажирських місць
     */
    public Taxi(int maxSeats) {
        super(maxSeats);
    }

    /**
     * Повертає назву типу транспорту.
     *
     * @return «Таксі»
     */
    @Override
    public String getTypeName() {
        return "Таксі";
    }
}
