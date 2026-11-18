package ua.kpi.java.task10.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Тексти інтерфейсу поточною мовою: обгортка над {@link ResourceBundle} «location.messages».
 * <p>
 * Файли мов лежать у папці {@code resources/location}: {@code messages.properties} (базовий, українською),
 * {@code messages_uk.properties}, {@code messages_en.properties} і {@code messages_de.properties}.
 * Кожне значення — шаблон {@link MessageFormat}: параметри записуються як {@code {0}}, {@code {1}},
 * а апостроф — подвоєним ({@code ''}).
 * <p>
 * Пакет шукається без переходу до мови системи ({@link ResourceBundle.Control#getNoFallbackControl}):
 * для локалі без власного файлу (наприклад, французької) береться базовий файл, незалежно від того,
 * яка мова встановлена в Windows.
 */
public class Messages {

    /** Базове ім'я пакета ресурсів: папка location, файли messages*.properties. */
    public static final String BUNDLE_NAME = "location.messages";

    /** Мови, для яких є переклад інтерфейсу (у порядку пунктів меню «Вибір мови»). */
    public static final List<Locale> SUPPORTED = List.of(Locale.of("uk"), Locale.of("en"), Locale.of("de"));

    private static final Logger LOG = Logger.getLogger(Messages.class.getName());
    private static final ResourceBundle.Control NO_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private Locale locale;
    private ResourceBundle bundle;

    /**
     * @param locale початкова мова інтерфейсу
     */
    public Messages(Locale locale) {
        setLocale(locale);
    }

    /**
     * Змінює мову інтерфейсу: завантажує пакет ресурсів для заданої локалі.
     *
     * @param locale нова мова
     */
    public void setLocale(Locale locale) {
        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, NO_FALLBACK);
        this.locale = locale;
        String file = bundle.getLocale().toString().isEmpty() ? "messages.properties"
                : "messages_" + bundle.getLocale() + ".properties";
        LOG.config("Завантажено пакет ресурсів для локалі " + locale + ": " + file);
    }

    /** @return поточна мова інтерфейсу */
    public Locale getLocale() {
        return locale;
    }

    /** @return пакет ресурсів поточної мови */
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Повертає повідомлення поточною мовою, підставивши параметри у шаблон.
     *
     * @param key  ключ повідомлення у файлах messages*.properties
     * @param args параметри для {@code {0}}, {@code {1}}, …
     * @return готовий текст
     * @throws java.util.MissingResourceException якщо ключа немає ні у файлі мови, ні в базовому
     */
    public String get(String key, Object... args) {
        return new MessageFormat(bundle.getString(key), locale).format(args);
    }

    /**
     * Назва мови нею самою з великої літери («Українська», «English», «Deutsch»).
     *
     * @param language мова
     * @return назва мови
     */
    public static String nativeName(Locale language) {
        String name = language.getDisplayLanguage(language);
        return name.substring(0, 1).toUpperCase(language) + name.substring(1);
    }
}
