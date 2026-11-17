package ua.kpi.java.task3.view;

import ua.kpi.java.task3.model.Book;

/**
 * Представлення: увесь вивід на консоль (меню, повідомлення, таблиці книг).
 */
public class BookView {

    private static final String ROW_FORMAT = "%-3s %-35s %-17s %-25s %5s %6s %10s%n";
    private static final String LINE = "-".repeat(107);

    /** Виводить меню програми. */
    public void printMenu() {
        System.out.println();
        System.out.println("========== МЕНЮ ==========");
        System.out.println("1. Книги зазначеного автора");
        System.out.println("2. Книги зазначеного видавництва");
        System.out.println("3. Книги, видані пізніше зазначеного року");
        System.out.println("4. Відсортувати книги за видавництвами");
        System.out.println("0. Вихід");
        System.out.print("Ваш вибір: ");
    }

    /**
     * Виводить заголовок обраного пункту меню.
     *
     * @param title назва пункту
     */
    public void printSection(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
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
        System.out.printf(ROW_FORMAT, "№", "Назва", "Автор", "Видавництво", "Рік", "Стор.", "Ціна, грн");
        System.out.println(LINE);
        for (int i = 0; i < books.length; i++) {
            Book book = books[i];
            System.out.printf(ROW_FORMAT, i + 1, book.getTitle(), book.getAuthor(), book.getPublisher(),
                    book.getYear(), book.getPages(), String.format("%.2f", book.getPrice()));
        }
        System.out.println(LINE);
        System.out.println("Усього книг: " + books.length);
    }
}
