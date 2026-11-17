package ua.kpi.java.task3.model;

import java.util.Arrays;

/**
 * Модель: зберігає масив книг і виконує над ним пошук та сортування.
 */
public class BookModel {

    private final Book[] books;

    /**
     * @param books масив книг, з яким працює модель
     */
    public BookModel(Book[] books) {
        this.books = books;
    }

    /** @return масив книг моделі */
    public Book[] getBooks() {
        return books;
    }

    /**
     * Повертає книги зазначеного автора (без урахування регістру).
     *
     * @param author автор
     * @return знайдені книги; порожній масив, якщо нічого не знайдено
     */
    public Book[] findByAuthor(String author) {
        return Arrays.stream(books)
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .toArray(Book[]::new);
    }

    /**
     * Повертає книги, видані зазначеним видавництвом (без урахування регістру).
     *
     * @param publisher видавництво
     * @return знайдені книги; порожній масив, якщо нічого не знайдено
     */
    public Book[] findByPublisher(String publisher) {
        return Arrays.stream(books)
                .filter(book -> book.getPublisher().equalsIgnoreCase(publisher))
                .toArray(Book[]::new);
    }

    /**
     * Повертає книги, видані пізніше зазначеного року (рік видання строго більший).
     *
     * @param year рік
     * @return знайдені книги; порожній масив, якщо нічого не знайдено
     */
    public Book[] findPublishedAfter(int year) {
        return Arrays.stream(books)
                .filter(book -> book.getYear() > year)
                .toArray(Book[]::new);
    }

    /**
     * Впорядковує масив книг за видавництвами за допомогою {@link BookPublisherComparator}.
     */
    public void sortByPublisher() {
        Arrays.sort(books, new BookPublisherComparator());
    }
}
