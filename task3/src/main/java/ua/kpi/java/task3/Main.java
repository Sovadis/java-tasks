package ua.kpi.java.task3;

import ua.kpi.java.task3.controller.BookController;
import ua.kpi.java.task3.model.BookDataSource;
import ua.kpi.java.task3.model.BookModel;
import ua.kpi.java.task3.view.BookView;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Task 3.2: консольний застосунок «Книги» за шаблоном MVC.
 * Створює модель, представлення і контролер та запускає програму.
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
        BookDataSource dataSource = new BookDataSource();
        BookModel model = new BookModel(dataSource.createBooks());
        BookView view = new BookView();
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        new BookController(model, view, dataSource, scanner, echoInput).run();
    }
}
