package ua.kpi.java.task10.books.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Порівнює книги за видавництвом, а книги одного видавництва — за назвою.
 * Рядки порівнюються за правилами українського алфавіту (через {@link Collator}),
 * тому, наприклад, «І» стоїть після «И», а не перед «А».
 */
public class BookPublisherComparator implements Comparator<Book> {

    private final Collator collator = Collator.getInstance(Locale.of("uk", "UA"));

    @Override
    public int compare(Book first, Book second) {
        int byPublisher = collator.compare(first.getPublisher(), second.getPublisher());
        if (byPublisher != 0) {
            return byPublisher;
        }
        return collator.compare(first.getTitle(), second.getTitle());
    }
}
