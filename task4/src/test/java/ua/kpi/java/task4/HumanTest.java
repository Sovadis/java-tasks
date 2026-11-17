package ua.kpi.java.task4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ua.kpi.java.task4.people.Firefighter;
import ua.kpi.java.task4.people.Passenger;
import ua.kpi.java.task4.people.Policeman;

/**
 * Модульні тести класів людей.
 */
class HumanTest {

    @Test
    void eachKindHasNameAndRole() {
        assertEquals("Пасажир Іван", new Passenger("Іван").toString());
        assertEquals("Пожежник Петро", new Firefighter("Петро").toString());
        assertEquals("Поліцейський Олег", new Policeman("Олег").toString());
        assertEquals("Іван", new Passenger("Іван").getName());
    }

    @Test
    void namesakesAreDifferentPeople() {
        assertNotEquals(new Passenger("Іван"), new Passenger("Іван"));
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Passenger(" "));
        assertThrows(IllegalArgumentException.class, () -> new Firefighter(null));
    }
}
