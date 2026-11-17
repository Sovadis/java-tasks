package ua.kpi.java.task5.cipher;

import ua.kpi.java.task5.ConsoleInput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Підзавдання 3: демонстрація шифрування символьного потоку
 * методами {@link ShiftCipher} і фільтрами {@link EncryptingWriter} / {@link DecryptingReader}.
 */
public class CipherTask {

    private static final String DEFAULT_FILE = "cipher.txt";

    private final ConsoleInput input;

    /**
     * @param input введення з клавіатури
     */
    public CipherTask(ConsoleInput input) {
        this.input = input;
    }

    /** Запитує текст і ключ, шифрує й дешифрує текст методами та через файл. */
    public void run() {
        String text = input.readNonEmptyLine("Введіть текст для шифрування: ");
        char key = readKey();
        System.out.println("Код ключового символу '" + key + "': " + (int) key);

        String encrypted = ShiftCipher.encrypt(text, key);
        System.out.println("a) Зашифрований текст (метод encrypt): " + encrypted);
        char first = text.charAt(0);
        char firstEncrypted = encrypted.charAt(0);
        System.out.println("   Перший символ: '" + first + "' (код " + (int) first + ") + " + (int) key
                + " = '" + firstEncrypted + "' (код " + (int) firstEncrypted + ")");
        System.out.println("b) Розшифрований текст (метод decrypt): " + ShiftCipher.decrypt(encrypted, key));

        String path = input.readLineOrDefault(
                "c) Введіть шлях і ім'я файлу для зашифрованого тексту (Enter — " + DEFAULT_FILE + "): ",
                DEFAULT_FILE);
        File file = new File(path);
        try {
            writeEncrypted(file, text, key);
            System.out.println("   Текст записано через EncryptingWriter у файл: " + file.getAbsolutePath());
            System.out.println("   Вміст файлу (без дешифрування): " + readPlain(file));

            String decrypted = readDecrypted(file, key);
            System.out.println("   Прочитано через DecryptingReader: " + decrypted);
            System.out.println("   Результат збігається з вихідним текстом: " + (decrypted.equals(text) ? "так" : "ні"));
        } catch (IOException e) {
            System.out.println("Помилка роботи з файлом " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    /** Читає ключ: рівно один символ; інакше повторює запит. */
    private char readKey() {
        while (true) {
            String line = input.readLine("Введіть ключовий символ (один символ): ");
            if (line.length() == 1) {
                return line.charAt(0);
            }
            System.out.println("Помилка: «" + line + "» — потрібно ввести рівно один символ. Спробуйте ще раз.");
        }
    }

    /** Записує текст у файл через фільтр EncryptingWriter (кодування UTF-8). */
    private static void writeEncrypted(File file, String text, char key) throws IOException {
        try (Writer writer = new EncryptingWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)), key)) {
            writer.write(text);
        }
    }

    /** Читає файл через фільтр DecryptingReader. */
    private static String readDecrypted(File file, char key) throws IOException {
        try (Reader reader = new DecryptingReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), key)) {
            return readAll(reader);
        }
    }

    /** Читає файл як є, без дешифрування. */
    private static String readPlain(File file) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return readAll(reader);
        }
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[256];
        int count;
        while ((count = reader.read(buffer)) != -1) {
            result.append(buffer, 0, count);
        }
        return result.toString();
    }
}
