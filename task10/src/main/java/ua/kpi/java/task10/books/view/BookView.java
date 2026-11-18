package ua.kpi.java.task10.books.view;

import ua.kpi.java.task10.books.model.Book;
import ua.kpi.java.task10.i18n.Messages;

/**
 * Представлення: увесь вивід на консоль (меню, повідомлення, таблиці книг).
 * Скопійовано з Task 5; заголовки й пункти меню беруться з пакета ресурсів поточної мови,
 * а ціна форматується за правилами цієї мови (320,00 / 320.00). Дані книг не перекладаються.
 */
public class BookView {

    private static final String ROW_FORMAT = "%-3s %-35s %-17s %-25s %5s %6s %10s%n";
    private static final String LINE = "-".repeat(107);

    private final Messages messages;

    /**
     * @param messages тексти інтерфейсу поточною мовою
     */
    public BookView(Messages messages) {
        this.messages = messages;
    }

    /** Виводить меню роботи з книгами. */
    public void printMenu() {
        System.out.println();
        System.out.println(messages.get("books.menu.title"));
        for (int i = 1; i <= 8; i++) {
            System.out.println(i + ". " + messages.get("books.menu." + i));
        }
        System.out.println("0. " + messages.get("books.menu.0"));
    }

    /**
     * Виводить заголовок обраного пункту меню.
     *
     * @param number номер пункту меню
     */
    public void printSection(int number) {
        System.out.println();
        System.out.println("--- " + number + ". " + messages.get("books.menu." + number) + " ---");
    }

    /**
     * Виводить текстове повідомлення.
     *
     * @param message повідомлення
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Виводить масив книг у вигляді таблиці.
     *
     * @param title заголовок таблиці
     * @param books книги для виводу
     */
    public void printBooks(String title, Book[] books) {
        System.out.println(title);
        System.out.println(LINE);
        System.out.printf(ROW_FORMAT, "№", messages.get("table.title"), messages.get("table.author"),
                messages.get("table.publisher"), messages.get("table.year"), messages.get("table.pages"),
                messages.get("table.price"));
        System.out.println(LINE);
        for (int i = 0; i < books.length; i++) {
            Book book = books[i];
            System.out.printf(ROW_FORMAT, i + 1, book.getTitle(), book.getAuthor(), book.getPublisher(),
                    book.getYear(), book.getPages(), String.format(messages.getLocale(), "%.2f", book.getPrice()));
        }
        System.out.println(LINE);
        System.out.println(messages.get("table.total", books.length));
    }
}
