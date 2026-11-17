package ua.kpi.java.task4.vehicles;

import ua.kpi.java.task4.people.Human;

/**
 * Автобус — перевозить будь-яких людей, тому параметр типу зафіксовано як {@link Human}.
 */
public class Bus extends Vehicle<Human> {

    /**
     * Створює автобус.
     *
     * @param maxSeats кількість пасажирських місць
     */
    public Bus(int maxSeats) {
        super(maxSeats);
    }

    /**
     * Повертає назву типу транспорту.
     *
     * @return «Автобус»
     */
    @Override
    public String getTypeName() {
        return "Автобус";
    }
}
