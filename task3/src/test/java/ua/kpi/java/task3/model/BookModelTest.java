package ua.kpi.java.task3.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести пошуку та сортування в {@link BookModel} на фіксованому масиві (без випадковості).
 */
class BookModelTest {

    private BookModel model;

    @BeforeEach
    void setUp() {
        Book[] books = {
                new Book("Кобзар", "Тарас Шевченко", "Фоліо", 2021, 640, 320.00),
                new Book("Маруся Чурай", "Ліна Костенко", "А-БА-БА-ГА-ЛА-МА-ГА", 2014, 240, 260.00),
                new Book("Інтернат", "Сергій Жадан", "Meridian Czernowitz", 2017, 336, 250.00),
                new Book("Записки українського самашедшого", "Ліна Костенко", "А-БА-БА-ГА-ЛА-МА-ГА", 2011, 416, 290.00),
                new Book("Захар Беркут", "Іван Франко", "Фоліо", 2019, 256, 180.00),
                new Book("Ворошиловград", "Сергій Жадан", "Клуб сімейного дозвілля", 2010, 448, 230.00)
        };
        model = new BookModel(books);
    }

    @Test
    void findByAuthorReturnsAllBooksOfAuthor() {
        Book[] found = model.findByAuthor("Ліна Костенко");

        assertArrayEquals(new String[]{"Маруся Чурай", "Записки українського самашедшого"}, titles(found));
    }

    @Test
    void findByAuthorIgnoresCase() {
        assertEquals(2, model.findByAuthor("сергій жадан").length);
    }

    @Test
    void findByAuthorReturnsEmptyArrayWhenNotFound() {
        assertEquals(0, model.findByAuthor("Стівен Кінг").length);
    }

    @Test
    void findByPublisherReturnsBooksOfPublisher() {
        Book[] found = model.findByPublisher("Фоліо");

        assertArrayEquals(new String[]{"Кобзар", "Захар Беркут"}, titles(found));
    }

    @Test
    void findByPublisherReturnsEmptyArrayWhenNotFound() {
        assertEquals(0, model.findByPublisher("Наш формат").length);
    }

    @Test
    void findPublishedAfterReturnsOnlyStrictlyLaterBooks() {
        Book[] found = model.findPublishedAfter(2017);

        assertArrayEquals(new String[]{"Кобзар", "Захар Беркут"}, titles(found));
        assertTrue(Arrays.stream(found).allMatch(book -> book.getYear() > 2017));
    }

    @Test
    void findPublishedAfterReturnsEmptyArrayWhenNotFound() {
        assertEquals(0, model.findPublishedAfter(2021).length);
    }

    @Test
    void sortByPublisherOrdersByPublisherThenTitle() {
        model.sortByPublisher();

        assertArrayEquals(new String[]{
                "Інтернат",                          // Meridian Czernowitz (латиниця — першою)
                "Записки українського самашедшого",  // А-БА-БА-ГА-ЛА-МА-ГА
                "Маруся Чурай",                      // А-БА-БА-ГА-ЛА-МА-ГА
                "Ворошиловград",                     // Клуб сімейного дозвілля
                "Захар Беркут",                      // Фоліо
                "Кобзар"                             // Фоліо
        }, titles(model.getBooks()));
    }

    private static String[] titles(Book[] books) {
        return Arrays.stream(books).map(Book::getTitle).toArray(String[]::new);
    }
}
