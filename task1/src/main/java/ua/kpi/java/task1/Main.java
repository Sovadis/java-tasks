package ua.kpi.java.task1;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Консольний додаток до лабораторної роботи №1 (варіант 5).
 * Користувач вводить рядки зі словами, програма виводить слова, що складаються тільки з різних символів.
 *
 * <p>Необов'язковий аргумент {@code --echo} повторює кожен введений рядок у виводі
 * (потрібно лише тоді, коли ввід подається з файлу, щоб протокол роботи читався як діалог).
 */
public class Main {

    private static final String DEMO_TEXT =
            "Java is a programming language, кіт і собака грають у дворі, world peace, Ананас";

    public static void main(String[] args) {
        boolean echo = args.length > 0 && args[0].equals("--echo");
        System.out.println("Лабораторна робота №1, варіант 5.");
        System.out.println("Пошук слів, які складаються тільки з різних символів.");
        System.out.println();

        System.out.println("Демонстраційний рядок: " + DEMO_TEXT);
        printResult(DEMO_TEXT);

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        while (true) {
            System.out.println();
            System.out.print("Введіть рядок зі словами (порожній рядок — вихід): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();
            if (echo) {
                System.out.println(line);
            }
            if (line.isBlank()) {
                break;
            }
            printResult(line);
        }
        System.out.println("Роботу завершено.");
    }

    private static void printResult(String text) {
        String[] words = UniqueCharWords.splitWords(text);
        String[] result = UniqueCharWords.find(text);
        System.out.println("Слова (" + words.length + "): " + arrayToString(words));
        if (result.length == 0) {
            System.out.println("Слів, які складаються тільки з різних символів, не знайдено.");
        } else {
            System.out.println("Результат (" + result.length + "): " + arrayToString(result));
        }
    }

    /** Формує рядок виду [a, b, c] за допомогою циклу. */
    private static String arrayToString(String[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        return sb.append(']').toString();
    }
}
