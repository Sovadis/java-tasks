package ua.kpi.java.task10.books.controller;

import ua.kpi.java.task10.ConsoleInput;
import ua.kpi.java.task10.books.model.Book;
import ua.kpi.java.task10.books.model.BookModel;
import ua.kpi.java.task10.books.storage.BookFileStorage;
import ua.kpi.java.task10.books.view.BookView;
import ua.kpi.java.task10.i18n.Messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Контролер: читає команди і дані для пошуку з клавіатури, звертається до моделі
 * та сховища файлів і передає результати представленню.
 * <p>
 * Скопійовано з Task 5 і доповнено: хід виконання записується в журнал (INFO — у файл,
 * WARNING і SEVERE — також на консоль), а пункт 8 «Вибір мови» змінює мову інтерфейсу.
 * Записи журналу — технічні й завжди українською, тексти для користувача — мовою інтерфейсу.
 * У журнал шлях до файлу записується так, як його введено (робочу папку Main записує під час запуску).
 */
public class BookController {

    private static final Logger LOG = Logger.getLogger(BookController.class.getName());
    private static final String DEFAULT_FILE = "books.dat";

    private final BookModel model;
    private final BookView view;
    private final BookFileStorage storage;
    private final ConsoleInput input;
    private final Messages messages;

    /**
     * @param model    модель з масивом книг
     * @param view     представлення для виводу
     * @param storage  збереження і читання книг з файлу
     * @param input    введення з клавіатури
     * @param messages тексти інтерфейсу поточною мовою (спільні з представленням)
     */
    public BookController(BookModel model, BookView view, BookFileStorage storage, ConsoleInput input,
                          Messages messages) {
        this.model = model;
        this.view = view;
        this.storage = storage;
        this.input = input;
        this.messages = messages;
    }

    /** Виводить вихідний масив і запускає цикл меню до вибору «0». */
    public void run() {
        LOG.info("Відкрито меню «Книги», у пам'яті " + model.getBooks().length + " книг");
        view.printBooks(messages.get("books.initial"), model.getBooks());

        boolean running = true;
        while (running) {
            view.printMenu();
            String choice = input.readLine(messages.get("prompt.choice"));
            switch (choice) {
                case "1" -> showBooksByAuthor();
                case "2" -> showBooksByPublisher();
                case "3" -> showBooksPublishedAfter();
                case "4" -> sortBooksByPublisher();
                case "5" -> saveToFile();
                case "6" -> loadFromFile();
                case "7" -> showAllBooks();
                case "8" -> chooseLanguage();
                case "0" -> running = false;
                default -> {
                    LOG.info("Невідомий пункт меню «" + choice + "»");
                    view.printMessage(messages.get("menu.unknown", choice));
                }
            }
        }
        LOG.info("Закрито меню «Книги»");
    }

    private void showBooksByAuthor() {
        view.printSection(1);
        String author = input.readNonEmptyLine(messages.get("prompt.author"));
        Book[] found = model.findByAuthor(author);
        LOG.fine("Пошук за автором «" + author + "»: знайдено " + found.length);
        showResult(found, messages.get("books.byAuthor", author), messages.get("books.byAuthor.none", author));
    }

    private void showBooksByPublisher() {
        view.printSection(2);
        String publisher = input.readNonEmptyLine(messages.get("prompt.publisher"));
        Book[] found = model.findByPublisher(publisher);
        LOG.fine("Пошук за видавництвом «" + publisher + "»: знайдено " + found.length);
        showResult(found, messages.get("books.byPublisher", publisher),
                messages.get("books.byPublisher.none", publisher));
    }

    private void showBooksPublishedAfter() {
        view.printSection(3);
        int year = input.readInt(messages.get("prompt.year"));
        Book[] found = model.findPublishedAfter(year);
        LOG.fine("Пошук книг, виданих пізніше " + year + " року: знайдено " + found.length);
        showResult(found, messages.get("books.after", year), messages.get("books.after.none", year));
    }

    private void sortBooksByPublisher() {
        view.printSection(4);
        model.sortByPublisher();
        LOG.info("Набір книг впорядковано за видавництвами");
        view.printBooks(messages.get("books.sorted"), model.getBooks());
    }

    private void saveToFile() {
        view.printSection(5);
        String path = input.readLineOrDefault(messages.get("prompt.file", DEFAULT_FILE), DEFAULT_FILE);
        String fullPath = new File(path).getAbsolutePath();
        try {
            storage.save(model.getBooks(), path);
            LOG.info("Збережено " + model.getBooks().length + " книг у файл " + path);
            view.printMessage(messages.get("file.saved", model.getBooks().length, fullPath));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Не вдалося зберегти файл " + path, e);
            view.printMessage(messages.get("file.saveError", fullPath, e.getMessage()));
        }
    }

    private void loadFromFile() {
        view.printSection(6);
        String path = input.readLineOrDefault(messages.get("prompt.file", DEFAULT_FILE), DEFAULT_FILE);
        String fullPath = new File(path).getAbsolutePath();
        try {
            Book[] books = storage.load(path);
            model.setBooks(books);
            LOG.info("Прочитано " + books.length + " книг з файлу " + path);
            view.printBooks(messages.get("file.loaded", fullPath), books);
        } catch (FileNotFoundException e) {
            // очікувана помилка користувача: одним рядком, без стека викликів
            LOG.warning("Файл не знайдено: " + path);
            view.printMessage(messages.get("file.notFound", fullPath));
        } catch (IOException | ClassNotFoundException e) {
            LOG.severe("Файл " + path + " не містить набору книг: " + e);
            view.printMessage(messages.get("file.corrupted", fullPath, e.toString()));
        }
    }

    private void showAllBooks() {
        view.printSection(7);
        view.printBooks(messages.get("books.current"), model.getBooks());
    }

    private void chooseLanguage() {
        view.printSection(8);
        for (int i = 0; i < Messages.SUPPORTED.size(); i++) {
            System.out.println((i + 1) + ". " + Messages.nativeName(Messages.SUPPORTED.get(i)));
        }
        String choice = input.readLine(messages.get("prompt.choice"));
        int index;
        try {
            index = Integer.parseInt(choice) - 1;
        } catch (NumberFormatException e) {
            index = -1;
        }
        if (index < 0 || index >= Messages.SUPPORTED.size()) {
            LOG.info("Невідомий пункт меню мов «" + choice + "»");
            view.printMessage(messages.get("language.unknown", choice));
            return;
        }
        Locale old = messages.getLocale();
        messages.setLocale(Messages.SUPPORTED.get(index));
        LOG.info("Мову інтерфейсу змінено: " + old + " -> " + messages.getLocale());
        view.printMessage(messages.get("language.changed", Messages.nativeName(messages.getLocale())));
    }

    private void showResult(Book[] found, String title, String notFoundMessage) {
        if (found.length == 0) {
            view.printMessage(notFoundMessage);
        } else {
            view.printBooks(title, found);
        }
    }
}
