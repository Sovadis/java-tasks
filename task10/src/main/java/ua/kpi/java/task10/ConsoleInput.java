package ua.kpi.java.task10;

import ua.kpi.java.task10.i18n.Messages;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Читання даних з клавіатури (у кодуванні UTF-8) з перевіркою коректності введення.
 * Скопійовано з Task 5; повідомлення про помилки введення беруться з пакета ресурсів поточної мови.
 */
public class ConsoleInput {

    private static final Logger LOG = Logger.getLogger(ConsoleInput.class.getName());

    private final Scanner scanner;
    private final boolean echoInput;
    private final Messages messages;

    /**
     * @param in        потік, з якого читаються дані (зазвичай {@code System.in})
     * @param echoInput чи повторювати кожен введений рядок у виводі (коли введення подається з файлу)
     * @param messages  тексти інтерфейсу поточною мовою
     */
    public ConsoleInput(InputStream in, boolean echoInput, Messages messages) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.echoInput = echoInput;
        this.messages = messages;
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
        // FINEST нижчий за рівень реєстратора (FINE), тому цей запис відкидається одразу.
        LOG.finest(() -> "Прочитано рядок «" + line + "»");
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
            System.out.println(messages.get("input.empty"));
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
                LOG.info("Некоректне ціле число «" + line + "»");
                System.out.println(messages.get("input.notInteger", line));
            }
        }
    }
}
