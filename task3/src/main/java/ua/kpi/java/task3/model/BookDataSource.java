package ua.kpi.java.task3.model;

import java.util.Random;

/**
 * Заздалегідь підготовлені дані: значення для створення книг
 * і значення для пошуку, які обираються випадковим чином.
 */
public class BookDataSource {

    private static final String[] TITLES = {
            "Кобзар", "Захар Беркут", "Лісова пісня", "Тигролови",
            "Маруся Чурай", "Записки українського самашедшого", "Інтернат", "Ворошиловград",
            "1984", "Колгосп тварин", "Гаррі Поттер і філософський камінь", "Felix Austria"
    };
    private static final String[] AUTHORS = {
            "Тарас Шевченко", "Іван Франко", "Леся Українка", "Іван Багряний",
            "Ліна Костенко", "Ліна Костенко", "Сергій Жадан", "Сергій Жадан",
            "Джордж Орвелл", "Джордж Орвелл", "Джоан Роулінг", "Софія Андрухович"
    };
    private static final String[] PUBLISHERS = {
            "Фоліо", "Фоліо", "Ранок", "Фоліо",
            "А-БА-БА-ГА-ЛА-МА-ГА", "А-БА-БА-ГА-ЛА-МА-ГА", "Meridian Czernowitz", "Клуб сімейного дозвілля",
            "Клуб сімейного дозвілля", "Видавництво Старого Лева", "А-БА-БА-ГА-ЛА-МА-ГА", "Видавництво Старого Лева"
    };
    private static final int[] YEARS = {
            2021, 2019, 2020, 2018,
            2014, 2011, 2017, 2010,
            2015, 2020, 2002, 2014
    };
    private static final int[] PAGES = {
            640, 256, 192, 320,
            240, 416, 336, 448,
            320, 128, 320, 288
    };
    private static final double[] PRICES = {
            320.00, 180.00, 150.00, 210.00,
            260.00, 290.00, 250.00, 230.00,
            200.00, 170.00, 380.00, 240.00
    };

    /** Автори для пошуку: є й такі, яких немає в масиві (щоб показати «не знайдено»). */
    private static final String[] SEARCH_AUTHORS = {
            "Тарас Шевченко", "Ліна Костенко", "Сергій Жадан", "Джордж Орвелл",
            "Леся Українка", "Стівен Кінг", "Ольга Кобилянська"
    };
    /** Видавництва для пошуку: є й такі, яких немає в масиві. */
    private static final String[] SEARCH_PUBLISHERS = {
            "Фоліо", "А-БА-БА-ГА-ЛА-МА-ГА", "Видавництво Старого Лева", "Ранок",
            "Наш формат", "Vivat"
    };
    /** Роки для пошуку: для 2021 і 2023 пізніших книг немає. */
    private static final int[] SEARCH_YEARS = {2000, 2010, 2015, 2018, 2020, 2021, 2023};

    private final Random random;

    /** Створює джерело даних з новим генератором випадкових чисел. */
    public BookDataSource() {
        this(new Random());
    }

    /**
     * @param random генератор випадкових чисел (у тестах — з фіксованим зерном)
     */
    public BookDataSource(Random random) {
        this.random = random;
    }

    /**
     * Створює масив книг, беручи значення з підготовлених даних по порядку.
     *
     * @return новий масив книг
     */
    public Book[] createBooks() {
        Book[] books = new Book[TITLES.length];
        for (int i = 0; i < books.length; i++) {
            books[i] = new Book(TITLES[i], AUTHORS[i], PUBLISHERS[i], YEARS[i], PAGES[i], PRICES[i]);
        }
        return books;
    }

    /** @return випадково обраний автор для пошуку */
    public String randomAuthor() {
        return SEARCH_AUTHORS[random.nextInt(SEARCH_AUTHORS.length)];
    }

    /** @return випадково обране видавництво для пошуку */
    public String randomPublisher() {
        return SEARCH_PUBLISHERS[random.nextInt(SEARCH_PUBLISHERS.length)];
    }

    /** @return випадково обраний рік для пошуку */
    public int randomYear() {
        return SEARCH_YEARS[random.nextInt(SEARCH_YEARS.length)];
    }
}
