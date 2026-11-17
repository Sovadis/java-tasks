package ua.kpi.java.task5.words;

/**
 * Результат пошуку: рядок файлу та кількість слів у ньому.
 *
 * @param lineNumber номер рядка (починаючи з 1)
 * @param text       текст рядка
 * @param wordCount  кількість слів у рядку
 */
public record LineResult(int lineNumber, String text, int wordCount) {
}
