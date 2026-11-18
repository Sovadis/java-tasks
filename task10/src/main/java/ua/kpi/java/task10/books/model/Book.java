package ua.kpi.java.task10.books.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Книга: назва, автор, видавництво, рік видання, кількість сторінок і ціна.
 * Реалізує {@link Serializable}, щоб набір книг можна було зберегти у файл
 * об'єктними потоками із серіалізацією за замовчуванням.
 */
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String author;
    private final String publisher;
    private final int year;
    private final int pages;
    private final double price;

    /**
     * Створює книгу із заданими значеннями полів.
     *
     * @param title     назва
     * @param author    автор
     * @param publisher видавництво
     * @param year      рік видання
     * @param pages     кількість сторінок
     * @param price     ціна, грн
     */
    public Book(String title, String author, String publisher, int year, int pages, double price) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.price = price;
    }

    /** @return назва книги */
    public String getTitle() {
        return title;
    }

    /** @return автор книги */
    public String getAuthor() {
        return author;
    }

    /** @return видавництво */
    public String getPublisher() {
        return publisher;
    }

    /** @return рік видання */
    public int getYear() {
        return year;
    }

    /** @return кількість сторінок */
    public int getPages() {
        return pages;
    }

    /** @return ціна, грн */
    public double getPrice() {
        return price;
    }

    /** Книги рівні, якщо збігаються всі поля (потрібно для перевірки збереження й читання). */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Book book)) {
            return false;
        }
        return year == book.year
                && pages == book.pages
                && Double.compare(price, book.price) == 0
                && title.equals(book.title)
                && author.equals(book.author)
                && publisher.equals(book.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, year, pages, price);
    }

    @Override
    public String toString() {
        return String.format("«%s», %s, %s, %d р., %d с., %.2f грн",
                title, author, publisher, year, pages, price);
    }
}
