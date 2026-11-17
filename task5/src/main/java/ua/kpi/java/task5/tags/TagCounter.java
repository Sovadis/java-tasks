package ua.kpi.java.task5.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Підрахунок частоти тегів у HTML-тексті.
 * Рахуються лише відкриваючі теги ({@code <div>}, {@code <br/>}); назви тегів зводяться до нижнього регістру.
 * Закриваючі теги ({@code </div>}), коментарі ({@code <!-- -->}) і {@code <!DOCTYPE>} не рахуються,
 * а вміст {@code <script>} і {@code <style>} пропускається, щоб код на кшталт {@code a<b} не вважався тегом.
 */
public final class TagCounter {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern SCRIPT_OR_STYLE = Pattern.compile(
            "(<(script|style)\\b[^>]*>).*?</\\2\\s*>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern OPENING_TAG = Pattern.compile("<([a-zA-Z][a-zA-Z0-9-]*)");

    private TagCounter() {
    }

    /**
     * Рахує, скільки разів зустрічається кожен відкриваючий тег.
     *
     * @param html текст HTML-сторінки
     * @return відображення «назва тегу → кількість»
     */
    public static Map<String, Integer> countTags(String html) {
        String cleaned = COMMENT.matcher(html).replaceAll("");
        cleaned = SCRIPT_OR_STYLE.matcher(cleaned).replaceAll("$1");

        Map<String, Integer> counts = new HashMap<>();
        Matcher matcher = OPENING_TAG.matcher(cleaned);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase(Locale.ROOT);
            counts.merge(tag, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * a) Впорядковує результати за назвою тегу в лексикографічному порядку (за зростанням).
     *
     * @param counts результат {@link #countTags(String)}
     * @return список пар «тег — кількість»
     */
    public static List<Map.Entry<String, Integer>> sortByName(Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Map.Entry.comparingByKey());
        return entries;
    }

    /**
     * b) Впорядковує результати за частотою появи (за зростанням); теги з однаковою частотою — за назвою.
     *
     * @param counts результат {@link #countTags(String)}
     * @return список пар «тег — кількість»
     */
    public static List<Map.Entry<String, Integer>> sortByFrequency(Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Map.Entry.<String, Integer>comparingByValue()
                .thenComparing(Map.Entry.comparingByKey()));
        return entries;
    }
}
