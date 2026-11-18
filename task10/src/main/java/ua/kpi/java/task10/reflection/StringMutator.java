package ua.kpi.java.task10.reflection;

import java.lang.reflect.Field;

/**
 * Змінює значення вже створеного рядка через приватні поля класу {@link String} (рефлексія).
 * <p>
 * У JDK 8 рядок зберігав символи в полі {@code private final char[] value}. Починаючи з JDK 9
 * (JEP 254 «Compact Strings») поля такі:
 * <ul>
 *     <li>{@code byte[] value} — байти рядка;</li>
 *     <li>{@code byte coder} — кодування цих байтів: 0 = LATIN1 (один байт на символ, якщо всі символи
 *         з кодами до 255), 1 = UTF16 (два байти на символ, наприклад для кирилиці);</li>
 *     <li>{@code int hash} і {@code boolean hashIsZero} — кешований результат {@link String#hashCode()}.</li>
 * </ul>
 * Тому недостатньо підмінити лише масив: разом з ним копіюється {@code coder} рядка-джерела
 * (інакше байти UTF16 читалися б як LATIN1 і навпаки), а кеш хеш-коду скидається, щоб
 * {@code hashCode()} обчислився заново вже для нового вмісту.
 * <p>
 * Починаючи з JDK 16 пакет {@code java.lang} закритий для глибокої рефлексії, і
 * {@code setAccessible(true)} кидає {@link java.lang.reflect.InaccessibleObjectException}, якщо JVM
 * запущено без параметра {@code --add-opens java.base/java.lang=ALL-UNNAMED}.
 * <p>
 * Увага: рядки в Java незмінні за контрактом, і на це покладаються пул рядків, HashMap, JIT-компілятор.
 * Цей клас — лише навчальна демонстрація; змінений літерал змінюється для всієї програми.
 */
public final class StringMutator {

    /** Значення поля coder для однобайтового кодування. */
    public static final byte LATIN1 = 0;
    /** Значення поля coder для двобайтового кодування. */
    public static final byte UTF16 = 1;

    private StringMutator() {
    }

    /**
     * Підміняє вміст рядка {@code target} вмістом рядка {@code replacement}. Об'єкт {@code target}
     * залишається тим самим (та сама адреса), змінюються лише його приватні поля.
     *
     * @param target      рядок, який змінюється
     * @param replacement нове значення
     * @throws ReflectiveOperationException                   якщо поля не знайдено (інша версія JDK)
     * @throws java.lang.reflect.InaccessibleObjectException якщо java.lang не відкрито через --add-opens
     */
    public static void replaceValue(String target, String replacement) throws ReflectiveOperationException {
        Field value = accessibleField("value");
        Field coder = accessibleField("coder");
        Field hash = accessibleField("hash");
        Field hashIsZero = accessibleField("hashIsZero");

        // копія масиву: рядок-джерело не повинен ділити свій масив зі зміненим рядком
        byte[] newBytes = ((byte[]) value.get(replacement)).clone();
        value.set(target, newBytes);
        coder.setByte(target, coder.getByte(replacement));
        hash.setInt(target, 0);
        hashIsZero.setBoolean(target, false);
    }

    /**
     * Опис внутрішнього стану рядка: довжина, кодування, розмір масиву value та ідентифікатор об'єкта.
     *
     * @param s рядок
     * @return рядок на зразок «length = 13, coder = 1 (UTF16), value = byte[26], id = 1b6d3586»
     * @throws ReflectiveOperationException якщо поля не знайдено
     */
    public static String describe(String s) throws ReflectiveOperationException {
        byte coder = coderOf(s);
        int bytes = ((byte[]) accessibleField("value").get(s)).length;
        return String.format("length = %d, coder = %d (%s), value = byte[%d], id = %08x",
                s.length(), coder, coder == LATIN1 ? "LATIN1" : "UTF16", bytes, System.identityHashCode(s));
    }

    /**
     * Повертає значення приватного поля coder.
     *
     * @param s рядок
     * @return {@link #LATIN1} або {@link #UTF16}
     * @throws ReflectiveOperationException якщо поле не знайдено
     */
    public static byte coderOf(String s) throws ReflectiveOperationException {
        return accessibleField("coder").getByte(s);
    }

    private static Field accessibleField(String name) throws NoSuchFieldException {
        Field field = String.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
