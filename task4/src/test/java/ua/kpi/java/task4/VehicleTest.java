package ua.kpi.java.task4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

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

/**
 * Модульні тести наповнення транспортних засобів різними типами пасажирів.
 *
 * <p>Обмеження типів перевіряє компілятор, тому протестувати їх у JUnit неможливо.
 * Рядки нижче не скомпілюються, якщо їх розкоментувати:
 * <pre>
 * new FireTruck(2).board(new Passenger("Іван"));   // Passenger не є Firefighter
 * new PoliceCar(2).board(new Firefighter("Петро")); // Firefighter не є Policeman
 * </pre>
 */
class VehicleTest {

    @Test
    void newVehicleIsEmptyAndKnowsMaxSeats() {
        Bus bus = new Bus(30);

        assertEquals(30, bus.getMaxSeats());
        assertEquals(0, bus.getOccupiedSeats());
        assertTrue(bus.getPassengers().isEmpty());
    }

    @Test
    void busCarriesAllKindsOfPassengers() throws NoFreeSeatsException {
        Bus bus = new Bus(10);
        Passenger passenger = new Passenger("Іван");
        Firefighter firefighter = new Firefighter("Петро");
        Policeman policeman = new Policeman("Олег");

        bus.board(passenger);
        bus.board(firefighter);
        bus.board(policeman);

        assertEquals(3, bus.getOccupiedSeats());
        assertEquals(10, bus.getMaxSeats());
        assertEquals(List.of(passenger, firefighter, policeman), bus.getPassengers());
    }

    @Test
    void taxiCarriesAllKindsOfPassengers() throws NoFreeSeatsException {
        Taxi taxi = new Taxi(4);
        Passenger passenger = new Passenger("Марія");
        Firefighter firefighter = new Firefighter("Тарас");
        Policeman policeman = new Policeman("Сергій");

        taxi.board(passenger);
        taxi.board(firefighter);
        taxi.board(policeman);

        assertEquals(3, taxi.getOccupiedSeats());
        assertEquals(List.of(passenger, firefighter, policeman), taxi.getPassengers());
    }

    @Test
    void fireTruckCarriesFirefighters() throws NoFreeSeatsException {
        FireTruck fireTruck = new FireTruck(3);
        Firefighter first = new Firefighter("Андрій");
        Firefighter second = new Firefighter("Василь");

        fireTruck.board(first);
        fireTruck.board(second);

        assertEquals(2, fireTruck.getOccupiedSeats());
        assertEquals(List.of(first, second), fireTruck.getPassengers());
    }

    @Test
    void policeCarCarriesPolicemen() throws NoFreeSeatsException {
        PoliceCar policeCar = new PoliceCar(2);
        Policeman first = new Policeman("Дмитро");
        Policeman second = new Policeman("Максим");

        policeCar.board(first);
        policeCar.board(second);

        assertEquals(2, policeCar.getOccupiedSeats());
        assertEquals(List.of(first, second), policeCar.getPassengers());
    }

    @Test
    void boardingFullVehicleThrowsAndKeepsPassengers() throws NoFreeSeatsException {
        PoliceCar policeCar = new PoliceCar(2);
        policeCar.board(new Policeman("Дмитро"));
        policeCar.board(new Policeman("Максим"));

        assertThrows(NoFreeSeatsException.class, () -> policeCar.board(new Policeman("Олег")));
        assertEquals(2, policeCar.getOccupiedSeats());
    }

    @Test
    void boardingFullBusThrows() throws NoFreeSeatsException {
        Bus bus = new Bus(1);
        bus.board(new Passenger("Іван"));

        assertThrows(NoFreeSeatsException.class, () -> bus.board(new Firefighter("Петро")));
        assertEquals(1, bus.getOccupiedSeats());
    }

    @Test
    void disembarkFreesSeat() throws NoFreeSeatsException, PassengerNotFoundException {
        Taxi taxi = new Taxi(2);
        Passenger ivan = new Passenger("Іван");
        Policeman oleh = new Policeman("Олег");
        taxi.board(ivan);
        taxi.board(oleh);

        taxi.disembark(ivan);

        assertEquals(1, taxi.getOccupiedSeats());
        assertEquals(List.of(oleh), taxi.getPassengers());
    }

    @Test
    void freedSeatCanBeTakenAgain() throws NoFreeSeatsException, PassengerNotFoundException {
        FireTruck fireTruck = new FireTruck(1);
        Firefighter andriy = new Firefighter("Андрій");
        Firefighter vasyl = new Firefighter("Василь");
        fireTruck.board(andriy);

        fireTruck.disembark(andriy);
        fireTruck.board(vasyl);

        assertEquals(List.of(vasyl), fireTruck.getPassengers());
    }

    @Test
    void disembarkingAbsentPassengerThrows() throws NoFreeSeatsException {
        Bus bus = new Bus(5);
        bus.board(new Passenger("Іван"));

        assertThrows(PassengerNotFoundException.class, () -> bus.disembark(new Policeman("Олег")));
        assertEquals(1, bus.getOccupiedSeats());
    }

    @Test
    void disembarkingFromEmptyVehicleThrows() {
        PoliceCar policeCar = new PoliceCar(2);

        assertThrows(PassengerNotFoundException.class, () -> policeCar.disembark(new Policeman("Дмитро")));
    }

    @Test
    void disembarkingSamePassengerTwiceThrows() throws NoFreeSeatsException, PassengerNotFoundException {
        FireTruck fireTruck = new FireTruck(2);
        Firefighter taras = new Firefighter("Тарас");
        fireTruck.board(taras);
        fireTruck.disembark(taras);

        assertThrows(PassengerNotFoundException.class, () -> fireTruck.disembark(taras));
    }

    @Test
    void namesakeIsDifferentPassenger() throws NoFreeSeatsException {
        Bus bus = new Bus(5);
        bus.board(new Passenger("Іван"));

        assertThrows(PassengerNotFoundException.class, () -> bus.disembark(new Passenger("Іван")));
        assertEquals(1, bus.getOccupiedSeats());
    }

    @Test
    void samePassengerCannotBoardTwice() throws NoFreeSeatsException {
        Taxi taxi = new Taxi(4);
        Passenger maria = new Passenger("Марія");
        taxi.board(maria);

        assertThrows(IllegalArgumentException.class, () -> taxi.board(maria));
        assertEquals(1, taxi.getOccupiedSeats());
    }

    @Test
    void boardAllAcceptsListOfSubtype() throws NoFreeSeatsException {
        Bus bus = new Bus(5);
        List<Firefighter> fireTeam = List.of(new Firefighter("Тарас"), new Firefighter("Богдан"));
        List<Policeman> patrol = List.of(new Policeman("Олег"));

        bus.boardAll(fireTeam);
        bus.boardAll(patrol);

        assertEquals(3, bus.getOccupiedSeats());
    }

    @Test
    void boardAllWithoutEnoughSeatsBoardsNobody() throws NoFreeSeatsException {
        FireTruck fireTruck = new FireTruck(3);
        fireTruck.board(new Firefighter("Андрій"));
        List<Firefighter> team = List.of(new Firefighter("Василь"), new Firefighter("Григорій"),
                new Firefighter("Тарас"));

        assertThrows(NoFreeSeatsException.class, () -> fireTruck.boardAll(team));
        assertEquals(1, fireTruck.getOccupiedSeats());
    }

    @Test
    void passengersListCannotBeModifiedFromOutside() throws NoFreeSeatsException {
        Bus bus = new Bus(2);
        bus.board(new Passenger("Іван"));
        List<Human> passengers = bus.getPassengers();

        assertThrows(UnsupportedOperationException.class, () -> passengers.add(new Passenger("Марія")));
        assertEquals(1, bus.getOccupiedSeats());
    }

    @Test
    void vehicleNeedsAtLeastOneSeat() {
        assertThrows(IllegalArgumentException.class, () -> new Taxi(0));
        assertThrows(IllegalArgumentException.class, () -> new Bus(-1));
    }

    @Test
    void nullPassengerIsRejected() {
        Bus bus = new Bus(2);

        assertThrows(NullPointerException.class, () -> bus.board(null));
        assertEquals(0, bus.getOccupiedSeats());
    }
}
