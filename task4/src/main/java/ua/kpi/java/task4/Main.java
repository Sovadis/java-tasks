package ua.kpi.java.task4;

import java.util.List;

import ua.kpi.java.task4.exceptions.NoFreeSeatsException;
import ua.kpi.java.task4.exceptions.PassengerNotFoundException;
import ua.kpi.java.task4.people.Firefighter;
import ua.kpi.java.task4.people.Human;
import ua.kpi.java.task4.people.Passenger;
import ua.kpi.java.task4.people.Policeman;
import ua.kpi.java.task4.vehicles.Bus;
import ua.kpi.java.task4.vehicles.FireTruck;
import ua.kpi.java.task4.vehicles.PoliceCar;
import ua.kpi.java.task4.vehicles.Taxi;
import ua.kpi.java.task4.vehicles.Vehicle;

/**
 * Демонстрація завдання 4.1: транспорт, що перевозить різні типи пасажирів,
 * обмеження на основі generics, обидва винятки та підрахунок людей на дорозі.
 */
public class Main {

    /**
     * Точка входу програми.
     *
     * <p>Винятки, які демонструються навмисно, перехоплюються всередині. Решта
     * посадок і висадок у демонстрації коректні; якби якась із них не вдалася,
     * програма завершилася б із цим винятком.
     *
     * @param args аргументи командного рядка (не використовуються)
     * @throws NoFreeSeatsException якщо неочікувано забракло місць
     * @throws PassengerNotFoundException якщо неочікувано не знайдено пасажира
     */
    public static void main(String[] args) throws NoFreeSeatsException, PassengerNotFoundException {
        System.out.println("=== Завдання 4.1. Транспорт і пасажири (generics) ===");

        Bus bus = new Bus(5);
        Taxi taxi = new Taxi(3);
        FireTruck fireTruck = new FireTruck(4);
        PoliceCar policeCar = new PoliceCar(2);

        System.out.println();
        System.out.println("1. Посадка пасажирів");

        Passenger ivan = new Passenger("Іван");
        bus.board(ivan);
        bus.board(new Passenger("Марія"));
        // List<Firefighter> передається в Vehicle<Human> завдяки Collection<? extends T>.
        List<Firefighter> fireTeam = List.of(new Firefighter("Тарас"), new Firefighter("Богдан"));
        bus.boardAll(fireTeam);

        Passenger olena = new Passenger("Олена");
        taxi.board(olena);
        taxi.board(new Policeman("Олег"));
        taxi.board(new Firefighter("Петро"));

        fireTruck.boardAll(List.of(new Firefighter("Андрій"), new Firefighter("Василь"),
                new Firefighter("Григорій")));

        policeCar.board(new Policeman("Сергій"));
        policeCar.board(new Policeman("Дмитро"));

        // Обмеження перевіряє компілятор, тому рядки нижче НЕ скомпілюються:
        // fireTruck.board(ivan);                     // Passenger не є Firefighter
        // policeCar.board(new Firefighter("Петро")); // Firefighter не є Policeman

        printVehicle(bus);
        printVehicle(taxi);
        printVehicle(fireTruck);
        printVehicle(policeCar);

        System.out.println();
        System.out.println("2. Виняток: немає вільних місць");
        Policeman maksym = new Policeman("Максим");
        System.out.println("   Спроба посадки: " + maksym + " -> " + policeCar);
        try {
            policeCar.board(maksym);
        } catch (NoFreeSeatsException e) {
            System.out.println("   Перехоплено NoFreeSeatsException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("3. Виняток: пасажира немає в транспорті");
        System.out.println("   Спроба висадки з таксі: " + ivan + " (він їде в автобусі)");
        try {
            taxi.disembark(ivan);
        } catch (PassengerNotFoundException e) {
            System.out.println("   Перехоплено PassengerNotFoundException: " + e.getMessage());
        }
        Passenger namesake = new Passenger("Іван");
        System.out.println("   Спроба висадки з автобуса: " + namesake + " (тезка — інший об'єкт з тим самим ім'ям)");
        try {
            bus.disembark(namesake);
        } catch (PassengerNotFoundException e) {
            System.out.println("   Перехоплено PassengerNotFoundException: " + e.getMessage());
            System.out.println("   (пасажири порівнюються за ідентичністю об'єкта, а не за ім'ям)");
        }

        System.out.println();
        System.out.println("4. Підрахунок людей на ділянці дороги");
        Road road = new Road();
        road.addCarToRoad(bus);
        road.addCarToRoad(taxi);
        road.addCarToRoad(fireTruck);
        road.addCarToRoad(policeCar);
        System.out.println("   Транспорту на дорозі: " + road.carsInRoad.size());
        for (Vehicle<? extends Human> car : road.carsInRoad) {
            System.out.println("   - " + car);
        }
        System.out.println("   Усього людей на дорозі: " + road.getCountOfHumans());

        taxi.disembark(olena);
        System.out.println("   Висадка з таксі: " + olena);
        System.out.println("   Усього людей на дорозі: " + road.getCountOfHumans());
    }

    /**
     * Друкує транспорт, кількість місць і список пасажирів. Параметр має тип
     * {@code Vehicle<? extends Human>}, тому метод приймає будь-який транспорт.
     *
     * @param vehicle транспорт для друку
     */
    private static void printVehicle(Vehicle<? extends Human> vehicle) {
        System.out.println("   " + vehicle.getTypeName() + ": місць " + vehicle.getMaxSeats()
                + ", зайнято " + vehicle.getOccupiedSeats() + " -> " + vehicle.getPassengers());
    }
}
