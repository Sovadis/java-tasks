package ua.kpi.java.task1;

/**
 * Варіант 5: пошук слів, які складаються тільки з різних символів.
 *
 * <p>Словом вважається послідовність символів між пробілами та іншими символами «white space».
 * Розділові знаки на початку та в кінці слова (наприклад, кома після слова) до слова не належать.
 * Великі й малі літери вважаються одним символом, тому слово «Ананас» не підходить.
 *
 * <p>Реалізація навмисно використовує лише цикли, масиви та рядки (без колекцій і stream API).
 */
public final class UniqueCharWords {

    private UniqueCharWords() {
    }

    /**
     * Повертає слова рядка {@code text}, у яких жоден символ не повторюється, у порядку появи.
     *
     * @param text рядок зі словами
     * @return масив знайдених слів (порожній, якщо таких слів немає)
     * @throws IllegalArgumentException якщо {@code text} дорівнює {@code null}
     */
    public static String[] find(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Рядок не може бути null");
        }
        String[] words = splitWords(text);

        // Перший прохід: рахуємо, скільки слів підходить, щоб створити масив потрібного розміру.
        int count = 0;
        for (String word : words) {
            if (hasOnlyUniqueChars(word)) {
                count++;
            }
        }

        // Другий прохід: заповнюємо масив результату.
        String[] result = new String[count];
        int index = 0;
        for (String word : words) {
            if (hasOnlyUniqueChars(word)) {
                result[index++] = word;
            }
        }
        return result;
    }

    /**
     * Перевіряє, що всі символи слова різні (без урахування регістру).
     *
     * @param word слово
     * @return {@code true}, якщо жоден символ не повторюється
     */
    public static boolean hasOnlyUniqueChars(String word) {
        for (int i = 0; i < word.length(); i++) {
            char current = Character.toLowerCase(word.charAt(i));
            for (int j = i + 1; j < word.length(); j++) {
                if (current == Character.toLowerCase(word.charAt(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Розбиває рядок на слова за символами «white space» і обрізає розділові знаки по краях слів.
     *
     * @param text рядок зі словами
     * @return масив слів без порожніх елементів
     */
    public static String[] splitWords(String text) {
        // Перший прохід: рахуємо слова, щоб знати розмір масиву.
        int count = 0;
        int i = 0;
        while (i < text.length()) {
            int start = skipWhitespace(text, i);
            int end = findWordEnd(text, start);
            if (start < end && !trimPunctuation(text.substring(start, end)).isEmpty()) {
                count++;
            }
            i = end;
        }

        // Другий прохід: виділяємо слова.
        String[] words = new String[count];
        int index = 0;
        i = 0;
        while (i < text.length()) {
            int start = skipWhitespace(text, i);
            int end = findWordEnd(text, start);
            if (start < end) {
                String word = trimPunctuation(text.substring(start, end));
                if (!word.isEmpty()) {
                    words[index++] = word;
                }
            }
            i = end;
        }
        return words;
    }

    private static int skipWhitespace(String text, int from) {
        int i = from;
        while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
            i++;
        }
        return i;
    }

    private static int findWordEnd(String text, int from) {
        int i = from;
        while (i < text.length() && !Character.isWhitespace(text.charAt(i))) {
            i++;
        }
        return i;
    }

    /** Відкидає розділові знаки на початку та в кінці слова (апостроф і дефіс усередині лишаються). */
    private static String trimPunctuation(String word) {
        int start = 0;
        int end = word.length();
        while (start < end && !Character.isLetterOrDigit(word.charAt(start))) {
            start++;
        }
        while (end > start && !Character.isLetterOrDigit(word.charAt(end - 1))) {
            end--;
        }
        return word.substring(start, end);
    }
}
