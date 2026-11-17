package ua.kpi.java.task5.tags;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести підрахунку тегів на фіксованому HTML-тексті (без мережі).
 */
class TagCounterTest {

    private static final String HTML = """
            <!DOCTYPE html>
            <HTML lang="uk">
            <head>
              <meta charset="utf-8">
              <title>Тест</title>
              <!-- <div>цей тег у коментарі не рахується</div> -->
              <script>if (a<b && c<d) { x = "<span>"; }</script>
            </head>
            <body>
              <div class="a"><p>Перший</p><P>Другий<br/></p></div>
              <div><a href="#">посилання</a><img src="x.png" /></div>
            </body>
            </html>
            """;

    @Test
    void countsOpeningTagsCaseInsensitive() {
        Map<String, Integer> counts = TagCounter.countTags(HTML);

        assertEquals(Map.ofEntries(
                Map.entry("html", 1), Map.entry("head", 1), Map.entry("meta", 1),
                Map.entry("title", 1), Map.entry("script", 1), Map.entry("body", 1),
                Map.entry("div", 2), Map.entry("p", 2), Map.entry("br", 1),
                Map.entry("a", 1), Map.entry("img", 1)), counts);
    }

    @Test
    void ignoresClosingTagsCommentsDoctypeAndScriptContent() {
        Map<String, Integer> counts = TagCounter.countTags(HTML);

        assertFalse(counts.containsKey("!doctype"));
        assertFalse(counts.containsKey("span")); // всередині <script>
        assertFalse(counts.containsKey("b"));    // a<b у коді скрипту
        assertEquals(2, counts.get("div"));      // а не 3 з коментарем
    }

    @Test
    void returnsEmptyMapForTextWithoutTags() {
        assertTrue(TagCounter.countTags("Просто текст 1 < 2").isEmpty());
    }

    @Test
    void sortsByNameAscending() {
        List<Map.Entry<String, Integer>> sorted = TagCounter.sortByName(Map.of("p", 5, "a", 2, "div", 9));

        assertEquals(List.of("a", "div", "p"), sorted.stream().map(Map.Entry::getKey).toList());
    }

    @Test
    void sortsByFrequencyAscendingThenByName() {
        List<Map.Entry<String, Integer>> sorted =
                TagCounter.sortByFrequency(Map.of("p", 5, "span", 2, "a", 2, "div", 9));

        assertEquals(List.of("a", "span", "p", "div"), sorted.stream().map(Map.Entry::getKey).toList());
        assertEquals(List.of(2, 2, 5, 9), sorted.stream().map(Map.Entry::getValue).toList());
    }
}
