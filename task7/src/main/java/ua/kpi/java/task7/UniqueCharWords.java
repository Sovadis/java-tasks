package ua.kpi.java.task7;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Варіант 5, реалізований за допомогою lambda-виразів і Stream API:
 * пошук слів, які складаються тільки з різних символів.
 *
 * <p>Правила ті самі, що й у лабораторній роботі №1: слово — послідовність символів між символами
 * «white space», розділові знаки по краях слова відкидаються, регістр літер не враховується.
 */
public final class UniqueCharWords {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern EDGE_PUNCTUATION =
            Pattern.compile("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$");

    /** Lambda: відкидає розділові знаки на початку та в кінці слова. */
    public static final Function<String, String> TRIM_PUNCTUATION =
            word -> EDGE_PUNCTUATION.matcher(word).replaceAll("");

    /** Lambda: слово не порожнє. */
    public static final Predicate<String> NOT_EMPTY = word -> !word.isEmpty();

    /**
     * Lambda: усі символи слова різні (без урахування регістру) —
     * кількість різних символів дорівнює довжині слова.
     */
    public static final Predicate<String> HAS_ONLY_UNIQUE_CHARS =
            word -> word.toLowerCase().chars().distinct().count() == word.length();

    private UniqueCharWords() {
    }

    /**
     * Розбиває рядок на слова.
     *
     * @param text рядок зі словами
     * @return масив слів без порожніх елементів
     */
    public static String[] splitWords(String text) {
        requireText(text);
        return WHITESPACE.splitAsStream(text)
                .map(TRIM_PUNCTUATION)
                .filter(NOT_EMPTY)
                .toArray(String[]::new);
    }

    /**
     * Повертає слова рядка {@code text}, у яких жоден символ не повторюється, у порядку появи.
     *
     * @param text рядок зі словами
     * @return масив знайдених слів (порожній, якщо таких слів немає)
     */
    public static String[] find(String text) {
        return Arrays.stream(splitWords(text))
                .filter(HAS_ONLY_UNIQUE_CHARS)
                .toArray(String[]::new);
    }

    private static void requireText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Рядок не може бути null");
        }
    }
}
