package ua.kpi.java.task2;

import com.google.gson.Gson;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести методів {@code equals} і {@code hashCode} класу {@link Person}.
 */
class PersonTest {

    /** Повна перевірка контракту equals/hashCode бібліотекою EqualsVerifier. */
    @Test
    void equalsAndHashCodeFollowContract() {
        EqualsVerifier.forClass(Person.class).verify();
    }

    @Test
    void equalsIsReflexive() {
        Person person = new Person("Петренко", "Олена", 19);

        assertEquals(person, person);
    }

    @Test
    void equalsIsSymmetric() {
        Person first = new Person("Петренко", "Олена", 19);
        Person second = new Person("Петренко", "Олена", 19);

        assertEquals(first, second);
        assertEquals(second, first);
    }

    @Test
    void equalsIsTransitive() {
        Person a = new Person("Петренко", "Олена", 19);
        Person b = new Person("Петренко", "Олена", 19);
        Person c = new Person("Петренко", "Олена", 19);

        assertEquals(a, b);
        assertEquals(b, c);
        assertEquals(a, c);
    }

    @Test
    void equalPersonsHaveSameHashCode() {
        Person first = new Person("Петренко", "Олена", 19);
        Person second = new Person("Петренко", "Олена", 19);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void personIsNotEqualToNull() {
        Person person = new Person("Петренко", "Олена", 19);

        assertNotEquals(null, person);
    }

    @Test
    void personIsNotEqualToObjectOfAnotherClass() {
        Person person = new Person("Петренко", "Олена", 19);

        assertNotEquals("Петренко Олена 19", person);
    }

    @Test
    void personsWithDifferentLastNameAreNotEqual() {
        assertNotEquals(new Person("Петренко", "Олена", 19), new Person("Коваленко", "Олена", 19));
    }

    @Test
    void personsWithDifferentFirstNameAreNotEqual() {
        assertNotEquals(new Person("Петренко", "Олена", 19), new Person("Петренко", "Ірина", 19));
    }

    @Test
    void personsWithDifferentAgeAreNotEqual() {
        assertNotEquals(new Person("Петренко", "Олена", 19), new Person("Петренко", "Олена", 20));
    }

    @Test
    void nullFieldsAreComparedSafely() {
        assertEquals(new Person(null, null, 19), new Person(null, null, 19));
        assertNotEquals(new Person(null, "Олена", 19), new Person("Петренко", "Олена", 19));
    }

    @Test
    void personRestoredFromJsonIsEqualToOriginal() {
        Gson gson = new Gson();
        Person original = new Person("Петренко", "Олена", 19);

        String json = gson.toJson(original);
        Person restored = gson.fromJson(json, Person.class);

        assertTrue(json.contains("\"lastName\":\"Петренко\""));
        assertNotSame(original, restored);
        assertEquals(original, restored);
        assertEquals(original.hashCode(), restored.hashCode());
    }

    @Test
    void negativeAgeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Person("Петренко", "Олена", -1));
    }
}
