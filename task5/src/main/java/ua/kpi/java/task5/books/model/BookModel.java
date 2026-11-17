package ua.kpi.java.task5.books.model;

import java.util.Arrays;

/**
 * Модель: зберігає масив книг і виконує над ним пошук та сортування.
 * На відміну від Task 3, масив можна замінити набором, прочитаним з файлу.
 */
public class BookModel {

    private Book[] books;

    /**
     * @param books масив книг, з яким працює модель
     */
    public BookModel(Book[] books) {
        this.books = books;
    }

    /** @return поточний масив книг */
    public Book[] getBooks() {
        return books;
    }

    /**
     * Замінює поточний набір книг (наприклад, прочитаним з файлу).
     *
     * @param books новий масив книг
     */
    public void setBooks(Book[] books) {
        this.books = books;
    }

    /**
     * Повертає книги зазначеного автора (без урахування регістру та зайвих пробілів).
     *
     * @param author автор
     * @return знайдені книги; порожній масив, якщо нічого не знайдено
     */
    public Book[] findByAuthor(String author) {
        String wanted = author.trim();
        return Arrays.stream(books)
                .filter(book -> book.getAuthor().equalsIgnoreCase(wanted))
                .toArray(Book[]::new);
    }

    /**
     * Повертає книги, видані зазначеним видавництвом (без урахування регістру та зайвих пробілів).
     *
     * @param publisher видавництво
     * @return знайдені книги; порожній масив, якщо нічого не знайдено
     */
    public Book[] findByPublisher(String publisher) {
        String wanted = publisher.trim();
        return Arrays.stream(books)
                .filter(book -> book.getPublisher().equalsIgnoreCase(wanted))
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
