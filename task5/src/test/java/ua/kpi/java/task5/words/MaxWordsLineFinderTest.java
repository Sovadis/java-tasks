package ua.kpi.java.task5.words;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести пошуку рядка з максимальною кількістю слів.
 */
class MaxWordsLineFinderTest {

    @Test
    void countsWordsSeparatedBySpacesAndPunctuation() {
        assertEquals(4, MaxWordsLineFinder.countWords("Привіт, світе!  Hello world."));
    }

    @Test
    void doesNotCountDashAsWord() {
        assertEquals(3, MaxWordsLineFinder.countWords("Байти — це   числа"));
    }

    @Test
    void countsWordsWithApostropheAndHyphenAsOneWord() {
        assertEquals(3, MaxWordsLineFinder.countWords("м'ята, п’ять, введення-виведення"));
    }

    @Test
    void countsZeroWordsInEmptyLine() {
        assertEquals(0, MaxWordsLineFinder.countWords("   "));
        assertEquals(0, MaxWordsLineFinder.countWords(" — ... ! "));
    }

    @Test
    void findsLineWithMaxWords() throws IOException {
        String text = """
                Перший рядок
                Другий рядок має найбільше слів тут
                Третій рядок трохи довший
                """;

        LineResult result = MaxWordsLineFinder.find(new BufferedReader(new StringReader(text))).orElseThrow();

        assertEquals(2, result.lineNumber());
        assertEquals("Другий рядок має найбільше слів тут", result.text());
        assertEquals(6, result.wordCount());
    }

    @Test
    void returnsFirstLineWhenCountsAreEqual() throws IOException {
        String text = "один два\nтри чотири\n";

        LineResult result = MaxWordsLineFinder.find(new BufferedReader(new StringReader(text))).orElseThrow();

        assertEquals(1, result.lineNumber());
    }

    @Test
    void returnsEmptyResultWhenThereAreNoWords() throws IOException {
        Optional<LineResult> result = MaxWordsLineFinder.find(new BufferedReader(new StringReader("\n  \n—\n")));

        assertTrue(result.isEmpty());
    }

    @Test
    void readsUkrainianTextFromUtf8File(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("text.txt");
        Files.writeString(file, "Короткий рядок.\nЦей рядок у файлі найдовший за словами.\nКінець.\n",
                StandardCharsets.UTF_8);

        LineResult result = MaxWordsLineFinder.findInFile(file.toString()).orElseThrow();

        assertEquals(2, result.lineNumber());
        assertEquals(7, result.wordCount());
    }

    @Test
    void throwsWhenFileDoesNotExist(@TempDir Path dir) {
        String missing = dir.resolve("missing.txt").toString();

        assertThrows(FileNotFoundException.class, () -> MaxWordsLineFinder.findInFile(missing));
    }
}
