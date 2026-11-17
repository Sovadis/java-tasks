package ua.kpi.java.task5.books.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Тести моделі книг у Task 5: пошук за даними, введеними з клавіатури, і заміна набору.
 */
class BookModelTest {

    private BookModel model;

    @BeforeEach
    void setUp() {
        model = new BookModel(new Book[]{
                new Book("Кобзар", "Тарас Шевченко", "Фоліо", 2021, 640, 320.00),
                new Book("Маруся Чурай", "Ліна Костенко", "А-БА-БА-ГА-ЛА-МА-ГА", 2014, 240, 260.00),
                new Book("Захар Беркут", "Іван Франко", "Фоліо", 2019, 256, 180.00)
        });
    }

    @Test
    void findByAuthorIgnoresCaseAndExtraSpaces() {
        Book[] found = model.findByAuthor("  ліна костенко ");

        assertEquals(1, found.length);
        assertEquals("Маруся Чурай", found[0].getTitle());
    }

    @Test
    void findByPublisherAndYear() {
        assertEquals(2, model.findByPublisher("фоліо").length);
        assertEquals(2, model.findPublishedAfter(2014).length);
        assertEquals(0, model.findPublishedAfter(2021).length);
        assertEquals(0, model.findByAuthor("Стівен Кінг").length);
    }

    @Test
    void sortByPublisherOrdersBooks() {
        model.sortByPublisher();

        assertArrayEquals(new String[]{"Маруся Чурай", "Захар Беркут", "Кобзар"},
                Arrays.stream(model.getBooks()).map(Book::getTitle).toArray(String[]::new));
    }

    @Test
    void setBooksReplacesDataSet() {
        Book[] loaded = {new Book("1984", "Джордж Орвелл", "Клуб сімейного дозвілля", 2015, 320, 200.00)};

        model.setBooks(loaded);

        assertSame(loaded, model.getBooks());
        assertEquals(1, model.findByAuthor("Джордж Орвелл").length);
    }
}
