package ua.kpi.java.task10.logging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Налаштування системи логування java.util.logging з файлу ресурсів {@code logging.properties}.
 * <p>
 * Рівні, обробники й формат задано декларативно у файлі властивостей; програмно виконується лише те,
 * чого файл зробити не може: читання файлу з classpath і створення папки для журналу
 * (FileHandler не створює відсутні папки). Якщо JVM запущено з власним файлом
 * ({@code -Djava.util.logging.config.file=...}), він має пріоритет і ресурс не читається.
 */
public final class LoggingConfig {

    /** Спільний батьківський реєстратор програми; йому в logging.properties призначено обробники. */
    public static final String ROOT_LOGGER = "ua.kpi.java.task10";

    private static final String RESOURCE = "/logging.properties";
    private static final String FILE_PATTERN = "java.util.logging.FileHandler.pattern";

    /** Сильне посилання: LogManager тримає реєстратори через слабкі посилання. */
    private static Logger appLogger;

    private LoggingConfig() {
    }

    /**
     * Читає налаштування логування. Викликається на самому початку main, до створення реєстраторів.
     *
     * @return шлях до файлу журналу або {@code null}, якщо його неможливо визначити
     */
    public static Path init() {
        LogManager manager = LogManager.getLogManager();
        if (System.getProperty("java.util.logging.config.file") == null) {
            try (InputStream in = LoggingConfig.class.getResourceAsStream(RESOURCE)) {
                if (in == null) {
                    System.err.println("Не знайдено ресурс " + RESOURCE + ", логування за замовчуванням JDK");
                    return null;
                }
                manager.readConfiguration(in);
            } catch (IOException e) {
                System.err.println("Не вдалося прочитати " + RESOURCE + ": " + e.getMessage());
                return null;
            }
        }
        Path logFile = logFile(manager.getProperty(FILE_PATTERN));
        if (logFile != null && logFile.getParent() != null) {
            try {
                Files.createDirectories(logFile.getParent());
            } catch (IOException e) {
                System.err.println("Не вдалося створити папку журналу " + logFile.getParent());
            }
        }
        // обробники створюються під час першого звернення до реєстратора, тобто вже після створення папки
        appLogger = Logger.getLogger(ROOT_LOGGER);
        appLogger.config("Налаштування логування прочитано");
        return logFile;
    }

    /**
     * Перетворює шаблон FileHandler на шлях до файлу. Шаблони зі спецсимволами (%t, %h, %g, %u)
     * тут не розгортаються.
     *
     * @param pattern значення java.util.logging.FileHandler.pattern
     * @return абсолютний шлях або {@code null}
     */
    static Path logFile(String pattern) {
        if (pattern == null || pattern.isBlank() || pattern.contains("%")) {
            return null;
        }
        return Path.of(pattern).toAbsolutePath();
    }
}
