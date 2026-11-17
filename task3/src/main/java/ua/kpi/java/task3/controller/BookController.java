package ua.kpi.java.task3.controller;

import ua.kpi.java.task3.model.Book;
import ua.kpi.java.task3.model.BookDataSource;
import ua.kpi.java.task3.model.BookModel;
import ua.kpi.java.task3.view.BookView;

import java.util.Scanner;

/**
 * Контролер: читає вибір пункту меню, звертається до моделі
 * і передає результати представленню.
 */
public class BookController {

    private final BookModel model;
    private final BookView view;
    private final BookDataSource dataSource;
    private final Scanner scanner;
    private final boolean echoInput;

    /**
     * @param model      модель з масивом книг
     * @param view       представлення для виводу
     * @param dataSource джерело випадкових параметрів пошуку
     * @param scanner    джерело введення з клавіатури
     * @param echoInput  чи повторювати введені рядки у виводі (коли введення подається з файлу)
     */
    public BookController(BookModel model, BookView view, BookDataSource dataSource, Scanner scanner,
                          boolean echoInput) {
        this.model = model;
        this.view = view;
        this.dataSource = dataSource;
        this.scanner = scanner;
        this.echoInput = echoInput;
    }

    /** Виводить вихідний масив і запускає цикл меню до вибору «0». */
    public void run() {
        view.printBooks("Вихідний масив книг:", model.getBooks());

        boolean running = true;
        while (running) {
            view.printMenu();
            String choice = readChoice();
            switch (choice) {
                case "1" -> showBooksByAuthor();
                case "2" -> showBooksByPublisher();
                case "3" -> showBooksPublishedAfter();
                case "4" -> sortBooksByPublisher();
                case "0" -> running = false;
                default -> view.printMessage("Немає пункту меню «" + choice + "». Спробуйте ще раз.");
            }
        }
        view.printMessage("Роботу програми завершено.");
    }

    /** Читає вибір пункту меню; якщо введення завершилося, повертає «0» (вихід). */
    private String readChoice() {
        if (!scanner.hasNextLine()) {
            return "0";
        }
        String line = scanner.nextLine();
        if (echoInput) {
            view.printMessage(line);
        }
        return line.trim();
    }

    private void showBooksByAuthor() {
        view.printSection("1. Книги зазначеного автора");
        String author = dataSource.randomAuthor();
        view.printMessage("Випадково обраний автор: " + author);
        showResult(model.findByAuthor(author), "Книг автора «" + author + "» не знайдено.");
    }

    private void showBooksByPublisher() {
        view.printSection("2. Книги зазначеного видавництва");
        String publisher = dataSource.randomPublisher();
        view.printMessage("Випадково обране видавництво: " + publisher);
        showResult(model.findByPublisher(publisher), "Книг видавництва «" + publisher + "» не знайдено.");
    }

    private void showBooksPublishedAfter() {
        view.printSection("3. Книги, видані пізніше зазначеного року");
        int year = dataSource.randomYear();
        view.printMessage("Випадково обраний рік: " + year);
        showResult(model.findPublishedAfter(year), "Книг, виданих пізніше " + year + " року, не знайдено.");
    }

    private void sortBooksByPublisher() {
        view.printSection("4. Сортування книг за видавництвами");
        model.sortByPublisher();
        view.printBooks("Книги, впорядковані за видавництвами:", model.getBooks());
    }

    private void showResult(Book[] found, String notFoundMessage) {
        if (found.length == 0) {
            view.printMessage(notFoundMessage);
        } else {
            view.printBooks("Знайдені книги:", found);
        }
    }
}
