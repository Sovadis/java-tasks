package ua.kpi.java.task3.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести порядку, який задає {@link BookPublisherComparator}.
 */
class BookPublisherComparatorTest {

    private final BookPublisherComparator comparator = new BookPublisherComparator();

    @Test
    void comparesByPublisherFirst() {
        Book folio = book("А", "Фоліо");
        Book ranok = book("Я", "Ранок");

        assertTrue(comparator.compare(ranok, folio) < 0);
        assertTrue(comparator.compare(folio, ranok) > 0);
    }

    @Test
    void comparesByTitleWhenPublishersAreEqual() {
        Book kobzar = book("Кобзар", "Фоліо");
        Book berkut = book("Захар Беркут", "Фоліо");

        assertTrue(comparator.compare(berkut, kobzar) < 0);
    }

    @Test
    void returnsZeroForSamePublisherAndTitle() {
        assertEquals(0, comparator.compare(book("Кобзар", "Фоліо"), book("Кобзар", "Фоліо")));
    }

    @Test
    void usesUkrainianAlphabetOrder() {
        // У таблиці Unicode «І» (U+0406) стоїть перед «А» (U+0410), але в абетці — після «И».
        Book iskra = book("Книга", "Іскра");
        Book abetka = book("Книга", "Абетка");

        assertTrue(comparator.compare(abetka, iskra) < 0);
    }

    private static Book book(String title, String publisher) {
        return new Book(title, "Автор", publisher, 2020, 100, 100.0);
    }
}
