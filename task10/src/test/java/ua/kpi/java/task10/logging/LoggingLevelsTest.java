package ua.kpi.java.task10.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Перевіряє, які записи доходять до консолі й до файлу за рівнями з logging.properties.
 * Замість ConsoleHandler і FileHandler використано обробники, що запам'ятовують записи,
 * а замість глобального LogManager — анонімний реєстратор (тест не створює файлів журналу).
 */
class LoggingLevelsTest {

    /** Обробник, що запам'ятовує всі отримані від реєстратора записи і ті з них, які прийняв за рівнем. */
    private static final class CapturingHandler extends Handler {
        private final List<Level> received = new ArrayList<>();
        private final List<Level> levels = new ArrayList<>();

        CapturingHandler(Level level) {
            setLevel(level);
        }

        @Override
        public void publish(LogRecord record) {
            received.add(record.getLevel());
            if (isLoggable(record)) {
                levels.add(record.getLevel());
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    private Properties config;

    @BeforeEach
    void loadConfig() throws IOException {
        config = new Properties();
        try (InputStream in = LoggingLevelsTest.class.getResourceAsStream("/logging.properties")) {
            assertNotNull(in);
            config.load(in);
        }
    }

    private Level level(String key) {
        return Level.parse(config.getProperty(key).trim());
    }

    @Test
    void configuredLevelsMatchTheDesign() {
        assertEquals(Level.FINE, level("ua.kpi.java.task10.level"));
        assertEquals(Level.WARNING, level("java.util.logging.ConsoleHandler.level"));
        assertEquals(Level.INFO, level("java.util.logging.FileHandler.level"));
        assertEquals("logs/books.log", config.getProperty("java.util.logging.FileHandler.pattern").trim());
    }

    @Test
    void messagesAreFilteredByLoggerAndThenByEachHandler() {
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(level("ua.kpi.java.task10.level"));
        CapturingHandler console = new CapturingHandler(level("java.util.logging.ConsoleHandler.level"));
        CapturingHandler file = new CapturingHandler(level("java.util.logging.FileHandler.level"));
        logger.addHandler(console);
        logger.addHandler(file);

        List<Level> all = List.of(Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG,
                Level.FINE, Level.FINER, Level.FINEST);
        for (Level level : all) {
            logger.log(level, "запис рівня " + level);
        }

        // другий фільтр — рівень кожного обробника: CONFIG і FINE не прийняв жоден
        assertEquals(List.of(Level.SEVERE, Level.WARNING), console.levels);
        assertEquals(List.of(Level.SEVERE, Level.WARNING, Level.INFO), file.levels);
        // перший фільтр — реєстратор (FINE): FINER і FINEST не доходять навіть до обробників
        List<Level> passedLogger = List.of(Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE);
        assertEquals(passedLogger, console.received);
        assertEquals(passedLogger, file.received);
        assertFalse(logger.isLoggable(Level.FINER));
        assertTrue(logger.isLoggable(Level.FINE));
    }

    @Test
    void logFilePathIsResolvedOnlyForPlainPatterns() {
        assertEquals("books.log", LoggingConfig.logFile("logs/books.log").getFileName().toString());
        assertNull(LoggingConfig.logFile("%t/books%g.log"));
        assertNull(LoggingConfig.logFile(null));
    }
}
