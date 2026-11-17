package ua.kpi.java.task6;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * Консольний англо-український перекладач (завдання 6, Simple Task).
 * <p>
 * Програма створює {@link Translator}, заповнює словник початковими словами
 * і через меню дозволяє перекладати фрази, додавати пари слів з клавіатури
 * та переглядати словник.
 * <p>
 * Якщо запустити з аргументом {@code --echo}, програма повторює кожен введений
 * рядок у виводі — це зручно, коли вхідні дані подаються з файлу.
 */
public class Main {

    /** Початковий вміст словника: пари "англійське слово — переклад". */
    private static final String[][] DEFAULT_WORDS = {
            {"hello", "привіт"}, {"world", "світ"}, {"good", "добрий"},
            {"morning", "ранок"}, {"evening", "вечір"}, {"night", "ніч"},
            {"day", "день"}, {"my", "мій"}, {"dear", "дорогий"},
            {"friend", "друг"}, {"i", "я"}, {"we", "ми"},
            {"you", "ти"}, {"love", "люблю"}, {"summer", "літо"},
            {"winter", "зима"}, {"sea", "море"}, {"sun", "сонце"},
            {"and", "і"}, {"or", "або"}, {"cat", "кіт"},
            {"dog", "пес"}, {"big", "великий"}, {"little", "маленький"},
            {"black", "чорний"}, {"white", "білий"}, {"book", "книга"},
            {"student", "студент"}, {"teacher", "викладач"}, {"family", "сім'я"},
    };

    private static Scanner scanner;
    private static boolean echoInput;

    /**
     * Точка входу програми.
     *
     * @param args {@code --echo} — повторювати введені рядки у виводі
     */
    public static void main(String[] args) {
        echoInput = args.length > 0 && "--echo".equals(args[0]);
        scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        Translator translator = new Translator();
        for (String[] pair : DEFAULT_WORDS) {
            translator.addWord(pair[0], pair[1]);
        }

        System.out.println("=== Англо-український перекладач ===");
        System.out.println("Словник заповнено початковими словами. Кількість пар: " + translator.size());

        try {
            runMenu(translator);
        } catch (NoSuchElementException e) {
            // Введення закінчилося (наприклад, файл з даними прочитано повністю).
            System.out.println();
            System.out.println("Введення завершено. До побачення!");
        }
    }

    /** Показує меню і виконує обрані дії, доки користувач не обере вихід. */
    private static void runMenu(Translator translator) {
        while (true) {
            System.out.println();
            System.out.println("Меню:");
            System.out.println("  1 - Перекласти фразу");
            System.out.println("  2 - Додати пару слів");
            System.out.println("  3 - Показати словник");
            System.out.println("  0 - Вихід");
            String choice = readLine("Ваш вибір: ");

            switch (choice) {
                case "1" -> translatePhrase(translator);
                case "2" -> addWordPair(translator);
                case "3" -> showDictionary(translator);
                case "0" -> {
                    System.out.println("До побачення!");
                    return;
                }
                default -> System.out.println("Помилка: немає пункту меню \"" + choice + "\". Оберіть 0, 1, 2 або 3.");
            }
        }
    }

    /** Зчитує англійську фразу і виводить її переклад та невідомі слова. */
    private static void translatePhrase(Translator translator) {
        System.out.println("--- Переклад фрази ---");
        String phrase = readValid("Введіть фразу англійською: ",
                Translator::isValidPhrase,
                "фраза має містити хоча б одне англійське слово (латинськими літерами).");

        System.out.println("Оригінал: " + phrase);
        System.out.println("Переклад: " + translator.translate(phrase));

        List<String> unknownWords = translator.findUnknownWords(phrase);
        if (!unknownWords.isEmpty()) {
            System.out.println("Невідомі слова (позначені [ ]): " + String.join(", ", unknownWords));
            System.out.println("Їх можна додати до словника через пункт меню 2.");
        }
    }

    /** Зчитує з клавіатури пару слів і додає її до словника. */
    private static void addWordPair(Translator translator) {
        System.out.println("--- Додавання пари слів ---");
        String english = readValid("Англійське слово: ",
                Translator::isValidEnglishWord,
                "англійське слово має містити лише латинські літери (без цифр, пробілів і знаків).");
        String ukrainian = readValid("Український переклад: ",
                Translator::isValidUkrainianTranslation,
                "переклад має бути написаний українськими літерами (можна апостроф, дефіс, пробіл).");

        String previous = translator.addWord(english, ukrainian);
        if (previous == null) {
            System.out.println("Додано нову пару: " + english + " -> " + ukrainian);
        } else {
            System.out.println("Слово \"" + english + "\" вже було у словнику (переклад: " + previous + ").");
            System.out.println("Переклад оновлено: " + english + " -> " + ukrainian);
        }
    }

    /** Виводить словник, впорядкований за алфавітом. */
    private static void showDictionary(Translator translator) {
        System.out.println("--- Словник (кількість пар: " + translator.size() + ") ---");
        // HashMap не зберігає порядок, тому для виводу копіюємо словник у TreeMap.
        Map<String, String> sorted = new TreeMap<>(translator.getDictionary());
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            System.out.printf("  %-12s -> %s%n", entry.getKey(), entry.getValue());
        }
    }

    /**
     * Зчитує рядок і повторює запит, доки введення не стане правильним.
     *
     * @param prompt       запрошення до введення
     * @param isValid      перевірка правильності рядка
     * @param errorMessage опис помилки для користувача
     * @return правильний рядок (без пробілів на початку і в кінці)
     */
    private static String readValid(String prompt, Predicate<String> isValid, String errorMessage) {
        while (true) {
            String line = readLine(prompt);
            if (line.isEmpty()) {
                System.out.println("Помилка: введено порожній рядок. Спробуйте ще раз.");
            } else if (!isValid.test(line)) {
                System.out.println("Помилка: \"" + line + "\" — " + errorMessage + " Спробуйте ще раз.");
            } else {
                return line;
            }
        }
    }

    /**
     * Виводить запрошення і зчитує один рядок.
     *
     * @param prompt запрошення до введення
     * @return введений рядок без пробілів на початку і в кінці
     */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        if (echoInput) {
            System.out.println(line);
        }
        return line.trim();
    }
}
