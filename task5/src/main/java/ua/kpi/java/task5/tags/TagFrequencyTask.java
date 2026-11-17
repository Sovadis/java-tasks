package ua.kpi.java.task5.tags;

import ua.kpi.java.task5.ConsoleInput;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Підзавдання 4: діалог з користувачем для підрахунку частоти тегів на сторінці за URL.
 */
public class TagFrequencyTask {

    private static final String DEFAULT_URL = "https://uk.wikipedia.org/wiki/Java";

    private final ConsoleInput input;

    /**
     * @param input введення з клавіатури
     */
    public TagFrequencyTask(ConsoleInput input) {
        this.input = input;
    }

    /** Запитує URL, завантажує сторінку і виводить частоту тегів у двох порядках. */
    public void run() {
        String url = input.readLineOrDefault("Введіть URL сторінки (Enter — " + DEFAULT_URL + "): ", DEFAULT_URL);
        String html;
        try {
            html = PageLoader.load(url);
        } catch (IllegalArgumentException | MalformedURLException e) {
            System.out.println("Помилка: некоректний URL «" + url + "» (" + e.getMessage() + ")");
            return;
        } catch (UnknownHostException e) {
            System.out.println("Помилка: не вдалося знайти сервер " + e.getMessage() + ". Перевірте адресу та з'єднання з мережею.");
            return;
        } catch (IOException e) {
            System.out.println("Помилка завантаження сторінки: " + e.getMessage());
            return;
        }

        Map<String, Integer> counts = TagCounter.countTags(html);
        if (counts.isEmpty()) {
            System.out.println("На сторінці не знайдено жодного тегу.");
            return;
        }
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Сторінка " + url + ": символів — " + html.length() + ", тегів — " + total
                + ", різних тегів — " + counts.size());

        printTable("a) Теги в лексикографічному порядку (за зростанням):", TagCounter.sortByName(counts));
        printTable("b) Теги за частотою появи (за зростанням):", TagCounter.sortByFrequency(counts));
    }

    private static void printTable(String title, List<Map.Entry<String, Integer>> entries) {
        System.out.println();
        System.out.println(title);
        System.out.printf("%-12s %10s%n", "Тег", "Кількість");
        System.out.println("-".repeat(23));
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.printf("%-12s %10d%n", entry.getKey(), entry.getValue());
        }
    }
}
