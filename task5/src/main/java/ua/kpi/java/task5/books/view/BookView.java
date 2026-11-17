package ua.kpi.java.task5.books.view;

import ua.kpi.java.task5.books.model.Book;

/**
 * Представлення: увесь вивід на консоль (меню, повідомлення, таблиці книг).
 */
public class BookView {

    private static final String ROW_FORMAT = "%-3s %-35s %-17s %-25s %5s %6s %10s%n";
    private static final String LINE = "-".repeat(107);

    /** Виводить меню роботи з книгами. */
    public void printMenu() {
        System.out.println();
        System.out.println("========== МЕНЮ «КНИГИ» ==========");
        System.out.println("1. Книги зазначеного автора");
        System.out.println("2. Книги зазначеного видавництва");
        System.out.println("3. Книги, видані пізніше зазначеного року");
        System.out.println("4. Відсортувати книги за видавництвами");
        System.out.println("5. Зберегти набір книг у файл");
        System.out.println("6. Прочитати набір книг з файлу");
        System.out.println("7. Показати поточний набір книг");
        System.out.println("0. Повернутися до головного меню");
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
