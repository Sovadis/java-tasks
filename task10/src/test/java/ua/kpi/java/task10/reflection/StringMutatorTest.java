package ua.kpi.java.task10.reflection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести зміни рядка рефлексією. Кожен тест змінює власний унікальний рядок,
 * бо змінений літерал залишається зміненим до кінця роботи JVM.
 */
class StringMutatorTest {

    @Test
    void replacesValueOfStringCreatedAtRuntime() throws Exception {
        String typed = new StringBuilder("рядок").append(" з клавіатури").toString();
        String sameObject = typed;

        StringMutator.replaceValue(typed, "новий рядок");

        assertEquals("новий рядок", typed);
        assertSame(sameObject, typed);
        assertEquals(11, typed.length());
    }

    @Test
    void coderIsCopiedTogetherWithBytes() throws Exception {
        String cyrillic = new String("Київ");          // UTF16: два байти на символ
        String latin = new String("Kyiv");             // LATIN1: один байт на символ
        assertEquals(StringMutator.UTF16, StringMutator.coderOf(cyrillic));
        assertEquals(StringMutator.LATIN1, StringMutator.coderOf(latin));

        StringMutator.replaceValue(cyrillic, "Kyiv, Ukraine");
        StringMutator.replaceValue(latin, "Львів");

        assertEquals("Kyiv, Ukraine", cyrillic);
        assertEquals(StringMutator.LATIN1, StringMutator.coderOf(cyrillic));
        assertEquals("Львів", latin);
        assertEquals(StringMutator.UTF16, StringMutator.coderOf(latin));
        assertEquals('ь', latin.charAt(1));
    }

    @Test
    void cachedHashCodeIsRecalculated() throws Exception {
        String key = new String("ключ до зміни");
        int before = key.hashCode();               // значення кешується в полі hash

        StringMutator.replaceValue(key, "інший ключ");

        assertNotEquals(before, key.hashCode());
        assertEquals("інший ключ".hashCode(), key.hashCode());
    }

    @Test
    void changingLiteralChangesEveryUseOfThatLiteral() throws Exception {
        String literal = "літерал для тесту пулу";
        String sameLiteral = "літерал для тесту пулу";
        assertSame(literal, sameLiteral);           // обидві змінні — один об'єкт з пулу рядків

        StringMutator.replaceValue(literal, "змінений літерал");

        assertEquals("змінений літерал", sameLiteral);
        assertEquals("змінений літерал", poolLiteral());
        assertEquals("змінений літерал", "літерал для тесту пулу".intern());
    }

    @Test
    void stringCreatedAtRuntimeIsNotThePooledLiteral() throws Exception {
        String literal = "окремий об'єкт";
        String typed = new String("окремий об'єкт");  // новий об'єкт, не з пулу

        StringMutator.replaceValue(typed, "змінено лише копію");

        assertEquals("окремий об'єкт", literal);
        assertEquals("змінено лише копію", typed);
    }

    @Test
    void describeShowsInternalState() throws Exception {
        String text = new String("Привіт");
        String description = StringMutator.describe(text);

        assertTrue(description.startsWith("length = 6, coder = 1 (UTF16), value = byte[12]"), description);
    }

    private static String poolLiteral() {
        return "літерал для тесту пулу";
    }
}
