package ua.kpi.java.task5.words;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Пошук рядка з максимальною кількістю слів у текстовому файлі.
 * Слово — послідовність літер або цифр, яка може містити апостроф чи дефіс
 * («м'ята», «введення-виведення»). Розділові знаки (наприклад, тире) словами не вважаються.
 */
public final class MaxWordsLineFinder {

    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]+(?:['’ʼ-][\\p{L}\\p{N}]+)*");

    private MaxWordsLineFinder() {
    }

    /**
     * Рахує кількість слів у рядку.
     *
     * @param line рядок тексту
     * @return кількість слів
     */
    public static int countWords(String line) {
        Matcher matcher = WORD.matcher(line);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Знаходить рядок з максимальною кількістю слів (за однакової кількості — перший).
     *
     * @param reader джерело тексту
     * @return знайдений рядок або порожній результат, якщо в тексті немає жодного слова
     * @throws IOException помилка читання
     */
    public static Optional<LineResult> find(BufferedReader reader) throws IOException {
        LineResult best = null;
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            int words = countWords(line);
            if (words > 0 && (best == null || words > best.wordCount())) {
                best = new LineResult(lineNumber, line, words);
            }
        }
        return Optional.ofNullable(best);
    }

    /**
     * Знаходить рядок з максимальною кількістю слів у файлі (кодування UTF-8).
     *
     * @param path шлях до файлу
     * @return знайдений рядок або порожній результат
     * @throws java.io.FileNotFoundException якщо файл не існує
     * @throws IOException                   помилка читання
     */
    public static Optional<LineResult> findInFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            return find(reader);
        }
    }
}
