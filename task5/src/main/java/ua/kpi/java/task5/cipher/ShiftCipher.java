package ua.kpi.java.task5.cipher;

/**
 * Шифр зсуву: кожен символ замінюється символом, код якого більший на код ключового символу.
 * Тип {@code char} 16-бітний, тому сума береться за модулем 65536 (переповнення «загортається»),
 * а дешифрування (віднімання коду ключа) точно відновлює вихідний символ.
 */
public final class ShiftCipher {

    private ShiftCipher() {
    }

    /**
     * a) Шифрує один символ.
     *
     * @param symbol символ тексту
     * @param key    ключовий символ
     * @return зашифрований символ (код symbol + код key за модулем 65536)
     */
    public static char encrypt(char symbol, char key) {
        return (char) (symbol + key);
    }

    /**
     * b) Дешифрує один символ.
     *
     * @param symbol зашифрований символ
     * @param key    ключовий символ
     * @return вихідний символ (код symbol − код key за модулем 65536)
     */
    public static char decrypt(char symbol, char key) {
        return (char) (symbol - key);
    }

    /**
     * a) Шифрує рядок посимвольно.
     *
     * @param text текст
     * @param key  ключовий символ
     * @return зашифрований текст
     */
    public static String encrypt(String text, char key) {
        char[] result = text.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] = encrypt(result[i], key);
        }
        return new String(result);
    }

    /**
     * b) Дешифрує рядок посимвольно.
     *
     * @param text зашифрований текст
     * @param key  ключовий символ
     * @return вихідний текст
     */
    public static String decrypt(String text, char key) {
        char[] result = text.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] = decrypt(result[i], key);
        }
        return new String(result);
    }
}
