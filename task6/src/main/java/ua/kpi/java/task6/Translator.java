package ua.kpi.java.task6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Перекладач з англійської мови на українську.
 * <p>
 * Словник зберігається в колекції {@link HashMap}: ключ — англійське слово
 * (завжди малими літерами), значення — український переклад.
 * Переклад пословний: кожне англійське слово фрази замінюється перекладом зі словника,
 * а всі інші символи (пробіли, розділові знаки, цифри) залишаються без змін.
 * Слова, яких немає у словнику, залишаються як є і беруться у квадратні дужки.
 */
public class Translator {

    /** Англійське слово — одна або більше латинських літер. */
    private static final Pattern ENGLISH_WORD = Pattern.compile("[A-Za-z]+");

    /**
     * Український переклад: починається з української літери, далі можуть бути
     * українські літери, апостроф, дефіс або пробіл (наприклад, "сім'я", "до побачення").
     */
    private static final Pattern UKRAINIAN_TRANSLATION =
            Pattern.compile("[а-щьюяґєіїА-ЩЬЮЯҐЄІЇ][а-щьюяґєіїА-ЩЬЮЯҐЄІЇ'’ʼ -]*");

    /** Словник: англійське слово (малими літерами) -> український переклад. */
    private final HashMap<String, String> dictionary = new HashMap<>();

    /**
     * Додає пару слів у словник. Якщо слово вже є, його переклад замінюється.
     *
     * @param english   англійське слово (лише латинські літери, регістр не важливий)
     * @param ukrainian український переклад
     * @return попередній переклад цього слова або {@code null}, якщо слова не було
     * @throws IllegalArgumentException якщо слово чи переклад мають неправильний формат
     */
    public String addWord(String english, String ukrainian) {
        if (!isValidEnglishWord(english)) {
            throw new IllegalArgumentException(
                    "Англійське слово має містити лише латинські літери: " + english);
        }
        if (!isValidUkrainianTranslation(ukrainian)) {
            throw new IllegalArgumentException(
                    "Переклад має бути написаний українськими літерами: " + ukrainian);
        }
        return dictionary.put(english.trim().toLowerCase(Locale.ROOT), ukrainian.trim());
    }

    /**
     * Перекладає англійську фразу українською.
     * Пошук у словнику не залежить від регістру; якщо слово в оригіналі
     * починається з великої літери, переклад теж починається з великої.
     *
     * @param phrase фраза англійською мовою
     * @return перекладена фраза; невідомі слова позначені як {@code [word]}
     * @throws IllegalArgumentException якщо фраза не містить жодного англійського слова
     */
    public String translate(String phrase) {
        if (!isValidPhrase(phrase)) {
            throw new IllegalArgumentException("Фраза має містити хоча б одне англійське слово.");
        }
        Matcher matcher = ENGLISH_WORD.matcher(phrase);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String translatedWord = translateWord(matcher.group());
            // Текст між словами копіюється як є, а саме слово замінюється перекладом.
            matcher.appendReplacement(result, Matcher.quoteReplacement(translatedWord));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Повертає слова фрази, яких немає у словнику (без повторів, у порядку появи).
     *
     * @param phrase фраза англійською мовою
     * @return список невідомих слів (порожній, якщо всі слова відомі)
     */
    public List<String> findUnknownWords(String phrase) {
        Set<String> unknown = new LinkedHashSet<>();
        if (phrase != null) {
            Matcher matcher = ENGLISH_WORD.matcher(phrase);
            while (matcher.find()) {
                String word = matcher.group();
                if (!dictionary.containsKey(word.toLowerCase(Locale.ROOT))) {
                    unknown.add(word);
                }
            }
        }
        return new ArrayList<>(unknown);
    }

    /**
     * Повертає словник лише для читання.
     *
     * @return незмінне представлення словника
     */
    public Map<String, String> getDictionary() {
        return Collections.unmodifiableMap(dictionary);
    }

    /**
     * Повертає кількість пар слів у словнику.
     *
     * @return розмір словника
     */
    public int size() {
        return dictionary.size();
    }

    /**
     * Перевіряє, чи рядок є правильним англійським словом (лише латинські літери).
     *
     * @param word рядок для перевірки
     * @return {@code true}, якщо формат правильний
     */
    public static boolean isValidEnglishWord(String word) {
        return word != null && ENGLISH_WORD.matcher(word.trim()).matches();
    }

    /**
     * Перевіряє, чи рядок є правильним українським перекладом.
     *
     * @param translation рядок для перевірки
     * @return {@code true}, якщо формат правильний
     */
    public static boolean isValidUkrainianTranslation(String translation) {
        return translation != null && UKRAINIAN_TRANSLATION.matcher(translation.trim()).matches();
    }

    /**
     * Перевіряє, чи фраза містить хоча б одне англійське слово.
     *
     * @param phrase фраза для перевірки
     * @return {@code true}, якщо фразу можна перекладати
     */
    public static boolean isValidPhrase(String phrase) {
        return phrase != null && ENGLISH_WORD.matcher(phrase).find();
    }

    /** Перекладає одне слово з урахуванням великої першої літери. */
    private String translateWord(String word) {
        String translation = dictionary.get(word.toLowerCase(Locale.ROOT));
        if (translation == null) {
            return "[" + word + "]";
        }
        if (Character.isUpperCase(word.charAt(0))) {
            return Character.toUpperCase(translation.charAt(0)) + translation.substring(1);
        }
        return translation;
    }
}
