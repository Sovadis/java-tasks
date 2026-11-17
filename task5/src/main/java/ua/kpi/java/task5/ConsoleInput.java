package ua.kpi.java.task5;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Читання даних з клавіатури (у кодуванні UTF-8) з перевіркою коректності введення.
 * Один об'єкт використовується в усій програмі, щоб не створювати кілька Scanner для System.in.
 */
public class ConsoleInput {

    private final Scanner scanner;
    private final boolean echoInput;

    /**
     * @param in        потік, з якого читаються дані (зазвичай {@code System.in})
     * @param echoInput чи повторювати кожен введений рядок у виводі (коли введення подається з файлу)
     */
    public ConsoleInput(InputStream in, boolean echoInput) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.echoInput = echoInput;
    }

    /**
     * Виводить підказку і читає рядок без пробілів на початку й у кінці.
     *
     * @param prompt підказка
     * @return введений рядок
     * @throws java.util.NoSuchElementException якщо введення завершилося
     */
    public String readLine(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        if (echoInput) {
            System.out.println(line);
        }
        return line.trim();
    }

    /**
     * Читає рядок; якщо введено порожній рядок, повертає значення за замовчуванням.
     *
     * @param prompt       підказка
     * @param defaultValue значення, якщо користувач натиснув лише Enter
     * @return введений рядок або значення за замовчуванням
     */
    public String readLineOrDefault(String prompt, String defaultValue) {
        String line = readLine(prompt);
        return line.isEmpty() ? defaultValue : line;
    }

    /**
     * Читає непорожній рядок; повторює запит, доки рядок порожній.
     *
     * @param prompt підказка
     * @return непорожній рядок
     */
    public String readNonEmptyLine(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Помилка: рядок не може бути порожнім. Спробуйте ще раз.");
        }
    }

    /**
     * Читає ціле число; при некоректному введенні виводить повідомлення і повторює запит.
     *
     * @param prompt підказка
     * @return введене ціле число
     */
    public int readInt(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Помилка: «" + line + "» не є цілим числом. Спробуйте ще раз.");
            }
        }
    }
}
