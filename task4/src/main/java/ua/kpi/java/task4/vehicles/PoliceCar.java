package ua.kpi.java.task4.vehicles;

import ua.kpi.java.task4.people.Policeman;

/**
 * Поліцейська машина — перевозить лише поліцейських.
 *
 * <p>Обмеження задано параметром типу {@code Car<Policeman>}: метод
 * {@code board(Policeman)} не прийме звичайного пасажира чи пожежника,
 * і такий код просто не скомпілюється.
 */
public class PoliceCar extends Car<Policeman> {

    /**
     * Створює поліцейську машину.
     *
     * @param maxSeats кількість пасажирських місць
     */
    public PoliceCar(int maxSeats) {
        super(maxSeats);
    }

    /**
     * Повертає назву типу транспорту.
     *
     * @return «Поліцейська машина»
     */
    @Override
    public String getTypeName() {
        return "Поліцейська машина";
    }
}
