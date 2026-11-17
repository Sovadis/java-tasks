package ua.kpi.java.task3.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести підготовлених даних {@link BookDataSource}.
 */
class BookDataSourceTest {

    @Test
    void createsAtLeastTenFilledBooks() {
        Book[] books = new BookDataSource().createBooks();

        assertTrue(books.length >= 10);
        for (Book book : books) {
            assertNotNull(book);
            assertFalse(book.getTitle().isBlank());
            assertFalse(book.getAuthor().isBlank());
            assertFalse(book.getPublisher().isBlank());
        }
    }

    @Test
    void randomSearchValuesAreNotEmpty() {
        BookDataSource dataSource = new BookDataSource(new Random(42));

        for (int i = 0; i < 20; i++) {
            assertFalse(dataSource.randomAuthor().isBlank());
            assertFalse(dataSource.randomPublisher().isBlank());
            assertTrue(dataSource.randomYear() > 0);
        }
    }
}
