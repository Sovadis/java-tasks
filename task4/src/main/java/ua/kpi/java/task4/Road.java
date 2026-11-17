package ua.kpi.java.task4;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.kpi.java.task4.people.Human;
import ua.kpi.java.task4.vehicles.Vehicle;

/**
 * Ділянка дороги, на якій рухаються транспортні засоби різних типів.
 *
 * <p>Тут потрібен wildcard {@code Vehicle<? extends Human>}. Generics інваріантні:
 * {@code FireTruck} — це {@code Vehicle<Firefighter>}, і він НЕ є підтипом
 * {@code Vehicle<Human>}, тому список {@code List<Vehicle<Human>>} не прийняв би
 * пожежну чи поліцейську машину. Сирий тип {@code List<Vehicle>} (як у заготовці)
 * працює, але вимикає перевірку типів. Wildcard дозволяє зберігати в одному
 * списку транспорт з будь-яким типом пасажирів і водночас лишає код типобезпечним:
 * через {@code Vehicle<? extends Human>} можна читати дані (кількість місць,
 * пасажирів як {@code Human}), але не можна нікого посадити — компілятор не знає,
 * який саме тип пасажира допустимий.
 */
public class Road {

    /** Транспортні засоби, що зараз перебувають на ділянці дороги. */
    public final List<Vehicle<? extends Human>> carsInRoad = new ArrayList<>();

    /**
     * Підраховує кількість людей, яких перевозить увесь транспорт на ділянці дороги
     * (сума зайнятих пасажирських місць; водії в моделі не враховуються).
     *
     * @return загальна кількість пасажирів
     */
    public int getCountOfHumans() {
        int count = 0;
        for (Vehicle<? extends Human> car : carsInRoad) {
            count += car.getOccupiedSeats();
        }
        return count;
    }

    /**
     * Додає транспортний засіб на ділянку дороги.
     *
     * @param car транспорт з будь-яким типом пасажирів
     * @throws NullPointerException якщо {@code car} дорівнює {@code null}
     */
    public void addCarToRoad(Vehicle<? extends Human> car) {
        carsInRoad.add(Objects.requireNonNull(car, "Транспорт не може бути null"));
    }
}
