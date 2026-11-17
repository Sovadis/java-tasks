package ua.kpi.java.task5.books.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.kpi.java.task5.books.model.Book;
import ua.kpi.java.task5.books.model.BookDataSource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести збереження і читання набору книг об'єктними потоками.
 */
class BookFileStorageTest {

    private final BookFileStorage storage = new BookFileStorage();

    @Test
    void savedBooksAreLoadedBackUnchanged(@TempDir Path dir) throws Exception {
        Book[] books = new BookDataSource().createBooks();
        String path = dir.resolve("books.dat").toString();

        storage.save(books, path);
        Book[] loaded = storage.load(path);

        assertNotSame(books, loaded);
        assertArrayEquals(books, loaded);
    }

    @Test
    void saveCreatesMissingFolders(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("нова папка").resolve("книги.dat");
        Book[] books = {new Book("Кобзар", "Тарас Шевченко", "Фоліо", 2021, 640, 320.0)};

        storage.save(books, file.toString());

        assertTrue(Files.exists(file));
        assertArrayEquals(books, storage.load(file.toString()));
    }

    @Test
    void loadThrowsWhenFileDoesNotExist(@TempDir Path dir) {
        String missing = dir.resolve("missing.dat").toString();

        assertThrows(FileNotFoundException.class, () -> storage.load(missing));
    }

    @Test
    void loadThrowsWhenFileIsNotObjectStream(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("text.dat");
        Files.writeString(file, "це звичайний текст, а не серіалізовані книги", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> storage.load(file.toString()));
    }

    @Test
    void loadThrowsWhenFileContainsOtherObject(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("string.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            out.writeObject("не масив книг");
        }

        assertThrows(IOException.class, () -> storage.load(file.toString()));
    }
}
