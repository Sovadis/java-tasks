package ua.kpi.java.task10.books.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.kpi.java.task10.ConsoleInput;
import ua.kpi.java.task10.books.model.BookDataSource;
import ua.kpi.java.task10.books.model.BookModel;
import ua.kpi.java.task10.books.storage.BookFileStorage;
import ua.kpi.java.task10.books.view.BookView;
import ua.kpi.java.task10.i18n.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тест пункту «Вибір мови»: після вибору мови меню, запрошення й таблиці виводяться цією мовою.
 */
class BookControllerTest {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    private Messages runWithInput(String lines) {
        Messages messages = new Messages(Locale.of("uk"));
        ConsoleInput input = new ConsoleInput(
                new ByteArrayInputStream(lines.getBytes(StandardCharsets.UTF_8)), false, messages);
        new BookController(new BookModel(new BookDataSource().createBooks()), new BookView(messages),
                new BookFileStorage(), input, messages).run();
        return messages;
    }

    @Test
    void languageChoiceSwitchesWholeMenu() {
        Messages messages = runWithInput("8\n2\n7\n0\n");    // English, показати книги, назад
        String text = output.toString(StandardCharsets.UTF_8);

        assertEquals(Locale.of("en"), messages.getLocale());
        assertTrue(text.contains("8. Вибір мови"));
        assertTrue(text.contains("Interface language changed: English"));
        assertTrue(text.contains("8. Language choice"));
        assertTrue(text.contains("Price, UAH"));
        assertTrue(text.contains("320.00"));                 // десятковий роздільник англійської мови
        assertTrue(text.contains("Total books: 12"));
    }

    @Test
    void unknownLanguageNumberKeepsCurrentLanguage() {
        Messages messages = runWithInput("8\n9\n0\n");
        String text = output.toString(StandardCharsets.UTF_8);

        assertEquals(Locale.of("uk"), messages.getLocale());
        assertTrue(text.contains("Немає мови з номером «9». Мову не змінено."));
        assertTrue(text.contains("320,00"));
    }
}
