package ua.kpi.java.task4.vehicles;

import ua.kpi.java.task4.people.Firefighter;

/**
 * Пожежна машина — перевозить лише пожежників.
 *
 * <p>Обмеження задано параметром типу {@code Car<Firefighter>}: метод
 * {@code board(Firefighter)} не прийме звичайного пасажира чи поліцейського,
 * і такий код просто не скомпілюється.
 */
public class FireTruck extends Car<Firefighter> {

    /**
     * Створює пожежну машину.
     *
     * @param maxSeats кількість пасажирських місць
     */
    public FireTruck(int maxSeats) {
        super(maxSeats);
    }

    /**
     * Повертає назву типу транспорту.
     *
     * @return «Пожежна машина»
     */
    @Override
    public String getTypeName() {
        return "Пожежна машина";
    }
}
