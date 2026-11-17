package ua.kpi.java.task5;

import ua.kpi.java.task5.books.controller.BookController;
import ua.kpi.java.task5.books.model.BookDataSource;
import ua.kpi.java.task5.books.model.BookModel;
import ua.kpi.java.task5.books.storage.BookFileStorage;
import ua.kpi.java.task5.books.view.BookView;
import ua.kpi.java.task5.cipher.CipherTask;
import ua.kpi.java.task5.tags.TagFrequencyTask;
import ua.kpi.java.task5.words.MaxWordsLineTask;

import java.util.NoSuchElementException;

/**
 * Task 5 (потоки введення-виведення): головне меню для вибору одного з чотирьох підзавдань.
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
        boolean echoInput = args.length > 0 && "--echo".equals(args[0]);
        ConsoleInput input = new ConsoleInput(System.in, echoInput);
        BookModel bookModel = new BookModel(new BookDataSource().createBooks());

        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = input.readLine("Ваш вибір: ");
                switch (choice) {
                    case "1" -> {
                        printTitle("1. Рядок з максимальною кількістю слів у файлі");
                        new MaxWordsLineTask(input).run();
                    }
                    case "2" -> {
                        printTitle("2. Книги: збереження у файл і читання з файлу");
                        new BookController(bookModel, new BookView(), new BookFileStorage(), input).run();
                    }
                    case "3" -> {
                        printTitle("3. Шифрування символьного потоку (FilterWriter / FilterReader)");
                        new CipherTask(input).run();
                    }
                    case "4" -> {
                        printTitle("4. Частота тегів на сторінці за URL");
                        new TagFrequencyTask(input).run();
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Немає пункту меню «" + choice + "». Спробуйте ще раз.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Введення завершено.");
        }
        System.out.println("Роботу програми завершено.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========== TASK 5: I/O STREAMS ==========");
        System.out.println("1. Рядок з максимальною кількістю слів у файлі");
        System.out.println("2. Книги (Task 3): збереження у файл і читання з файлу");
        System.out.println("3. Шифрування символьного потоку");
        System.out.println("4. Частота тегів на сторінці за URL");
        System.out.println("0. Вихід");
    }

    private static void printTitle(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }
}
