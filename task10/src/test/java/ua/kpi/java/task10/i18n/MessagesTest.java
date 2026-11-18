package ua.kpi.java.task10.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести пакетів ресурсів location/messages*.properties і класу {@link Messages}.
 */
class MessagesTest {

    private static final String[] FILES = {
            "messages.properties", "messages_uk.properties", "messages_en.properties", "messages_de.properties"
    };

    /** Читає файл мови напряму (без батьківського пакета), щоб бачити лише його власні ключі. */
    private static Properties load(String file) throws IOException {
        InputStream in = MessagesTest.class.getResourceAsStream("/location/" + file);
        assertNotNull(in, file);
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    @ParameterizedTest
    @ValueSource(strings = {"messages_uk.properties", "messages_en.properties", "messages_de.properties"})
    void everyLanguageHasExactlyTheKeysOfTheBaseBundle(String file) throws IOException {
        assertEquals(load("messages.properties").stringPropertyNames(), load(file).stringPropertyNames(), file);
    }

    @Test
    void everyValueIsValidMessageFormatAndPromptsEndWithSpace() throws IOException {
        for (String file : FILES) {
            Properties properties = load(file);
            for (String key : properties.stringPropertyNames()) {
                String pattern = properties.getProperty(key);
                assertDoesNotThrow(() -> new MessageFormat(pattern), file + ": " + key);
                if (key.startsWith("prompt.")) {
                    assertTrue(pattern.endsWith(": "), file + ": " + key);
                }
            }
        }
    }

    @Test
    void formatsParametersInEachLanguage() {
        Messages messages = new Messages(Locale.of("uk"));
        assertEquals("Набір із 12 книг збережено у файл: C:\\books.dat", messages.get("file.saved", 12, "C:\\books.dat"));
        assertEquals("Введіть розташування та ім'я файлу (Enter — books.dat): ",
                messages.get("prompt.file", "books.dat"));
        assertEquals("Книги, видані пізніше 2014 року:", messages.get("books.after", 2014));

        messages.setLocale(Locale.of("en"));
        assertEquals("A set of 12 books was saved to the file: C:\\books.dat",
                messages.get("file.saved", 12, "C:\\books.dat"));
        assertEquals("Books published after 2014:", messages.get("books.after", 2014));

        messages.setLocale(Locale.of("de"));
        assertEquals("12 Bücher wurden in der Datei gespeichert: C:\\books.dat",
                messages.get("file.saved", 12, "C:\\books.dat"));
        assertEquals("Ihre Wahl: ", messages.get("prompt.choice"));
    }

    @Test
    void bundleOfRequestedLanguageIsUsed() {
        Messages messages = new Messages(Locale.of("en", "US"));   // en_US → messages_en

        assertEquals(Locale.of("en"), messages.getBundle().getLocale());
        assertEquals("Language choice", messages.get("books.menu.8"));
    }

    @Test
    void unsupportedLanguageFallsBackToBaseBundle() {
        Messages messages = new Messages(Locale.FRENCH);            // messages_fr немає → messages

        assertEquals(Locale.ROOT, messages.getBundle().getLocale());
        assertEquals("Вибір мови", messages.get("books.menu.8"));
    }

    @Test
    void languagesAreNamedInThemselves() {
        assertEquals("Українська", Messages.nativeName(Locale.of("uk")));
        assertEquals("English", Messages.nativeName(Locale.of("en")));
        assertEquals("Deutsch", Messages.nativeName(Locale.of("de")));
    }
}
