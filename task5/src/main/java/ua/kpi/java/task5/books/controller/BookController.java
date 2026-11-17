package ua.kpi.java.task5.books.controller;

import ua.kpi.java.task5.ConsoleInput;
import ua.kpi.java.task5.books.model.Book;
import ua.kpi.java.task5.books.model.BookModel;
import ua.kpi.java.task5.books.storage.BookFileStorage;
import ua.kpi.java.task5.books.view.BookView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Контролер: читає команди і дані для пошуку з клавіатури, звертається до моделі
 * та сховища файлів і передає результати представленню.
 */
public class BookController {

    private static final String DEFAULT_FILE = "books.dat";

    private final BookModel model;
    private final BookView view;
    private final BookFileStorage storage;
    private final ConsoleInput input;

    /**
     * @param model   модель з масивом книг
     * @param view    представлення для виводу
     * @param storage збереження і читання книг з файлу
     * @param input   введення з клавіатури
     */
    public BookController(BookModel model, BookView view, BookFileStorage storage, ConsoleInput input) {
        this.model = model;
        this.view = view;
        this.storage = storage;
        this.input = input;
    }

    /** Виводить вихідний масив і запускає цикл меню до вибору «0». */
    public void run() {
        view.printBooks("Вихідний масив книг:", model.getBooks());

        boolean running = true;
        while (running) {
            view.printMenu();
            String choice = input.readLine("Ваш вибір: ");
            switch (choice) {
                case "1" -> showBooksByAuthor();
                case "2" -> showBooksByPublisher();
                case "3" -> showBooksPublishedAfter();
                case "4" -> sortBooksByPublisher();
                case "5" -> saveToFile();
                case "6" -> loadFromFile();
                case "7" -> showAllBooks();
                case "0" -> running = false;
                default -> view.printMessage("Немає пункту меню «" + choice + "». Спробуйте ще раз.");
            }
        }
    }

    private void showBooksByAuthor() {
        view.printSection("1. Книги зазначеного автора");
        String author = input.readNonEmptyLine("Введіть автора: ");
        showResult(model.findByAuthor(author),
                "Книги автора «" + author + "»:",
                "Книг автора «" + author + "» не знайдено.");
    }

    private void showBooksByPublisher() {
        view.printSection("2. Книги зазначеного видавництва");
        String publisher = input.readNonEmptyLine("Введіть видавництво: ");
        showResult(model.findByPublisher(publisher),
                "Книги видавництва «" + publisher + "»:",
                "Книг видавництва «" + publisher + "» не знайдено.");
    }

    private void showBooksPublishedAfter() {
        view.printSection("3. Книги, видані пізніше зазначеного року");
        int year = input.readInt("Введіть рік: ");
        showResult(model.findPublishedAfter(year),
                "Книги, видані пізніше " + year + " року:",
                "Книг, виданих пізніше " + year + " року, не знайдено.");
    }

    private void sortBooksByPublisher() {
        view.printSection("4. Сортування книг за видавництвами");
        model.sortByPublisher();
        view.printBooks("Книги, впорядковані за видавництвами:", model.getBooks());
    }

    private void saveToFile() {
        view.printSection("5. Збереження набору книг у файл");
        String path = input.readLineOrDefault(
                "Введіть розташування та ім'я файлу (Enter — " + DEFAULT_FILE + "): ", DEFAULT_FILE);
        String fullPath = new File(path).getAbsolutePath();
        try {
            storage.save(model.getBooks(), path);
            view.printMessage("Набір із " + model.getBooks().length + " книг збережено у файл: " + fullPath);
        } catch (IOException e) {
            view.printMessage("Помилка: не вдалося зберегти файл " + fullPath + " (" + e.getMessage() + ")");
        }
    }

    private void loadFromFile() {
        view.printSection("6. Читання набору книг з файлу");
        String path = input.readLineOrDefault(
                "Введіть розташування та ім'я файлу (Enter — " + DEFAULT_FILE + "): ", DEFAULT_FILE);
        String fullPath = new File(path).getAbsolutePath();
        try {
            Book[] books = storage.load(path);
            model.setBooks(books);
            view.printBooks("Набір книг прочитано з файлу " + fullPath + ":", books);
        } catch (FileNotFoundException e) {
            view.printMessage("Помилка: файл не знайдено або він недоступний: " + fullPath);
        } catch (IOException | ClassNotFoundException e) {
            view.printMessage("Помилка: файл " + fullPath + " пошкоджений або не містить набору книг (" + e + ")");
        }
    }

    private void showAllBooks() {
        view.printSection("7. Поточний набір книг");
        view.printBooks("Книги в пам'яті програми:", model.getBooks());
    }

    private void showResult(Book[] found, String title, String notFoundMessage) {
        if (found.length == 0) {
            view.printMessage(notFoundMessage);
        } else {
            view.printBooks(title, found);
        }
    }
}
