package ua.kpi.java.task11;

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

    /**
     * Читає непорожній рядок не довший за {@code maxLength} символів; інакше повторює запит.
     *
     * @param prompt    підказка
     * @param maxLength максимальна довжина рядка
     * @return введений рядок
     */
    public String readText(String prompt, int maxLength) {
        while (true) {
            String line = readLine(prompt);
            if (line.isEmpty()) {
                System.out.println("Помилка: рядок не може бути порожнім. Спробуйте ще раз.");
            } else if (line.length() > maxLength) {
                System.out.println("Помилка: введено " + line.length() + " символів, дозволено не більше "
                        + maxLength + ". Спробуйте ще раз.");
            } else {
                return line;
            }
        }
    }

    /**
     * Запитує підтвердження «так»/«ні»; на іншу відповідь повторює запит.
     *
     * @param prompt підказка
     * @return {@code true} для «так», {@code false} для «ні»
     */
    public boolean confirm(String prompt) {
        while (true) {
            String answer = readLine(prompt).toLowerCase();
            if (answer.equals("так") || answer.equals("т")) {
                return true;
            }
            if (answer.equals("ні") || answer.equals("н")) {
                return false;
            }
            System.out.println("Помилка: відповідайте «так» або «ні».");
        }
    }
}
