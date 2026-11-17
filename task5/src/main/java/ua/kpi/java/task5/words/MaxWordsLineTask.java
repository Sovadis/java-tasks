package ua.kpi.java.task5.words;

import ua.kpi.java.task5.ConsoleInput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

/**
 * Підзавдання 1: діалог з користувачем для пошуку рядка з максимальною кількістю слів.
 */
public class MaxWordsLineTask {

    private static final String DEFAULT_FILE = "data/text.txt";

    private final ConsoleInput input;

    /**
     * @param input введення з клавіатури
     */
    public MaxWordsLineTask(ConsoleInput input) {
        this.input = input;
    }

    /** Запитує шлях до файлу і виводить рядок з максимальною кількістю слів. */
    public void run() {
        String path = input.readLineOrDefault(
                "Введіть шлях до текстового файлу (Enter — " + DEFAULT_FILE + "): ", DEFAULT_FILE);
        String fullPath = new File(path).getAbsolutePath();
        try {
            Optional<LineResult> result = MaxWordsLineFinder.findInFile(path);
            System.out.println("Файл: " + fullPath);
            if (result.isPresent()) {
                LineResult line = result.get();
                System.out.println("Рядок з максимальною кількістю слів — №" + line.lineNumber()
                        + " (слів: " + line.wordCount() + "):");
                System.out.println(line.text());
            } else {
                System.out.println("У файлі немає жодного слова.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Помилка: файл не знайдено або він недоступний: " + fullPath);
        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
        }
    }
}
