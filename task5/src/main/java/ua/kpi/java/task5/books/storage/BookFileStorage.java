package ua.kpi.java.task5.books.storage;

import ua.kpi.java.task5.books.model.Book;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Робота з файлами: збереження і читання набору книг об'єктними потоками
 * ({@link ObjectOutputStream} / {@link ObjectInputStream}) із серіалізацією за замовчуванням.
 */
public class BookFileStorage {

    /**
     * Зберігає масив книг у файл. Якщо папки для файлу не існує, вона створюється.
     *
     * @param books масив книг
     * @param path  розташування та ім'я файлу
     * @throws IOException якщо файл не вдалося записати
     */
    public void save(Book[] books, String path) throws IOException {
        File parent = new File(path).getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("не вдалося створити папку " + parent);
        }
        // Файловий потік — окремий ресурс, щоб він закрився, навіть якщо конструктор ObjectOutputStream впаде.
        try (FileOutputStream file = new FileOutputStream(path);
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(file))) {
            out.writeObject(books);
        }
    }

    /**
     * Читає масив книг з файлу, записаного методом {@link #save(Book[], String)}.
     *
     * @param path розташування та ім'я файлу
     * @return прочитаний масив книг
     * @throws java.io.FileNotFoundException якщо файл не існує
     * @throws IOException                   якщо файл пошкоджений або не містить масиву книг
     * @throws ClassNotFoundException        якщо у файлі об'єкт невідомого класу
     */
    public Book[] load(String path) throws IOException, ClassNotFoundException {
        // Файловий потік — окремий ресурс: якщо файл не є об'єктним потоком, конструктор
        // ObjectInputStream кидає виняток, а файл однаково буде закрито.
        try (FileInputStream file = new FileInputStream(path);
             ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(file))) {
            Object data = in.readObject();
            if (data instanceof Book[] books) {
                return books;
            }
            throw new InvalidObjectException("файл не містить набору книг");
        }
    }
}
