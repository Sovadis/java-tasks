package ua.kpi.java.task7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Консольний додаток до лабораторної роботи №7 (варіант 5): пошук слів, які складаються
 * тільки з різних символів, за допомогою lambda-виразів.
 *
 * <p>Необов'язковий аргумент {@code --echo} повторює кожен введений рядок у виводі
 * (потрібно лише тоді, коли ввід подається з файлу, щоб протокол роботи читався як діалог).
 */
public class Main {

    private static final String DEMO_TEXT =
            "Java is a programming language, кіт і собака грають у дворі, world peace, Ананас";

    /** Lambda: обробляє один введений рядок і виводить результат. */
    private static final Consumer<String> PRINT_RESULT = text -> {
        String[] words = UniqueCharWords.splitWords(text);
        String[] result = UniqueCharWords.find(text);
        System.out.println("Слова (" + words.length + "): " + Arrays.toString(words));
        System.out.println(result.length == 0
                ? "Слів, які складаються тільки з різних символів, не знайдено."
                : "Результат (" + result.length + "): " + Arrays.toString(result));
    };

    public static void main(String[] args) throws IOException {
        boolean echo = args.length > 0 && args[0].equals("--echo");
        System.out.println("Лабораторна робота №7, варіант 5 (lambda).");
        System.out.println("Пошук слів, які складаються тільки з різних символів.");
        System.out.println();

        System.out.println("Демонстраційний рядок: " + DEMO_TEXT);
        PRINT_RESULT.accept(DEMO_TEXT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        Runnable prompt = () -> System.out.print("\nВведіть рядок зі словами (порожній рядок — вихід): ");
        prompt.run();
        reader.lines()
                .peek(line -> {
                    if (echo) {
                        System.out.println(line);
                    }
                })
                .takeWhile(line -> !line.isBlank())
                .forEach(line -> {
                    PRINT_RESULT.accept(line);
                    prompt.run();
                });
        System.out.println("Роботу завершено.");
    }
}
