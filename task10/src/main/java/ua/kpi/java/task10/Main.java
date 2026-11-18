package ua.kpi.java.task10;

import ua.kpi.java.task10.books.controller.BookController;
import ua.kpi.java.task10.books.model.BookDataSource;
import ua.kpi.java.task10.books.model.BookModel;
import ua.kpi.java.task10.books.storage.BookFileStorage;
import ua.kpi.java.task10.books.view.BookView;
import ua.kpi.java.task10.i18n.Messages;
import ua.kpi.java.task10.logging.LoggingConfig;
import ua.kpi.java.task10.reflection.ReflectionDemo;

import java.nio.file.Path;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * Task 10 (рефлексія, логування, інтернаціоналізація): головне меню вибору завдання.
 * <p>
 * Пункт 1 — завдання 10.1 (зміна рядка рефлексією), пункт 2 — застосунок «Книги» з Task 5
 * з логуванням (10.2) і вибором мови інтерфейсу (10.3). Головне меню теж перекладається.
 * <p>
 * Якщо запустити з аргументом {@code --echo}, програма повторює кожен введений
 * рядок у виводі — це зручно, коли вхідні дані подаються з файлу.
 */
public class Main {

    /**
     * Точка входу.
     *
     * @param args {@code --echo} — повторювати введені рядки у виводі
     */
    public static void main(String[] args) {
        // логування налаштовується до створення будь-якого реєстратора
        Path logFile = LoggingConfig.init();
        Logger log = Logger.getLogger(Main.class.getName());

        boolean echoInput = args.length > 0 && "--echo".equals(args[0]);
        Messages messages = new Messages(Locale.of("uk"));
        ConsoleInput input = new ConsoleInput(System.in, echoInput, messages);
        BookModel bookModel = new BookModel(new BookDataSource().createBooks());
        log.info("Програму запущено, мова інтерфейсу: " + messages.getLocale()
                + ", робоча папка: " + Path.of("").toAbsolutePath());
        if (logFile != null) {
            System.out.println(messages.get("app.logFile", logFile));
        }

        try {
            boolean running = true;
            while (running) {
                printMenu(messages);
                String choice = input.readLine(messages.get("prompt.choice"));
                switch (choice) {
                    case "1" -> {
                        printTitle(messages.get("main.menu.1"));
                        new ReflectionDemo(input).run();
                    }
                    case "2" -> {
                        printTitle(messages.get("main.menu.2"));
                        BookView view = new BookView(messages);
                        new BookController(bookModel, view, new BookFileStorage(), input, messages).run();
                    }
                    case "0" -> running = false;
                    default -> {
                        log.info("Невідомий пункт головного меню «" + choice + "»");
                        System.out.println(messages.get("menu.unknown", choice));
                    }
                }
            }
        } catch (NoSuchElementException e) {
            log.warning("Введення завершилося до вибору пункту «0»");
            System.out.println();
            System.out.println(messages.get("app.inputEnded"));
        }
        log.info("Програму завершено");
        System.out.println(messages.get("app.finished"));
    }

    private static void printMenu(Messages messages) {
        System.out.println();
        System.out.println(messages.get("main.menu.title"));
        System.out.println("1. " + messages.get("main.menu.1"));
        System.out.println("2. " + messages.get("main.menu.2"));
        System.out.println("0. " + messages.get("main.menu.0"));
    }

    private static void printTitle(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }
}
