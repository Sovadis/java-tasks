package ua.kpi.java.task4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ua.kpi.java.task4.exceptions.NoFreeSeatsException;
import ua.kpi.java.task4.exceptions.PassengerNotFoundException;
import ua.kpi.java.task4.people.Firefighter;
import ua.kpi.java.task4.people.Passenger;
import ua.kpi.java.task4.people.Policeman;
import ua.kpi.java.task4.vehicles.Bus;
import ua.kpi.java.task4.vehicles.FireTruck;
import ua.kpi.java.task4.vehicles.PoliceCar;
import ua.kpi.java.task4.vehicles.Taxi;

/**
 * Модульні тести підрахунку людей на ділянці дороги.
 */
class RoadTest {

    @Test
    void emptyRoadHasNoHumans() {
        Road road = new Road();

        assertEquals(0, road.getCountOfHumans());
        assertEquals(0, road.carsInRoad.size());
    }

    @Test
    void countsHumansInVehiclesOfDifferentTypes() throws NoFreeSeatsException {
        Bus bus = new Bus(10);
        bus.board(new Passenger("Іван"));
        bus.board(new Firefighter("Петро"));
        bus.board(new Policeman("Олег"));
        Taxi taxi = new Taxi(4);
        taxi.board(new Passenger("Марія"));
        FireTruck fireTruck = new FireTruck(4);
        fireTruck.boardAll(List.of(new Firefighter("Андрій"), new Firefighter("Василь")));
        PoliceCar policeCar = new PoliceCar(2);
        policeCar.board(new Policeman("Сергій"));

        Road road = new Road();
        road.addCarToRoad(bus);
        road.addCarToRoad(taxi);
        road.addCarToRoad(fireTruck);
        road.addCarToRoad(policeCar);

        assertEquals(4, road.carsInRoad.size());
        assertEquals(3 + 1 + 2 + 1, road.getCountOfHumans());
    }

    @Test
    void emptyVehicleAddsNothingToCount() throws NoFreeSeatsException {
        Taxi taxi = new Taxi(4);
        taxi.board(new Passenger("Марія"));
        Road road = new Road();
        road.addCarToRoad(taxi);
        road.addCarToRoad(new FireTruck(6));

        assertEquals(2, road.carsInRoad.size());
        assertEquals(1, road.getCountOfHumans());
    }

    @Test
    void countReflectsBoardingAndDisembarkingAfterAddingToRoad()
            throws NoFreeSeatsException, PassengerNotFoundException {
        PoliceCar policeCar = new PoliceCar(2);
        Bus bus = new Bus(5);
        Road road = new Road();
        road.addCarToRoad(policeCar);
        road.addCarToRoad(bus);

        Policeman dmytro = new Policeman("Дмитро");
        policeCar.board(dmytro);
        bus.board(new Passenger("Іван"));
        bus.board(new Firefighter("Тарас"));
        assertEquals(3, road.getCountOfHumans());

        policeCar.disembark(dmytro);
        assertEquals(2, road.getCountOfHumans());
    }

    @Test
    void nullVehicleIsRejected() {
        Road road = new Road();

        assertThrows(NullPointerException.class, () -> road.addCarToRoad(null));
        assertEquals(0, road.carsInRoad.size());
    }
}
