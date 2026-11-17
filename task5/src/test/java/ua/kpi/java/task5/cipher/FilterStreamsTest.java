package ua.kpi.java.task5.cipher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Тести фільтрів {@link EncryptingWriter} і {@link DecryptingReader}.
 */
class FilterStreamsTest {

    private static final String TEXT = "Шифр Цезаря для Java: «привіт», їжак і ґанок!";
    private static final char KEY = 'k';

    @Test
    void encryptingWriterWritesSameTextAsEncryptMethod() throws IOException {
        StringWriter target = new StringWriter();
        try (Writer writer = new EncryptingWriter(target, KEY)) {
            writer.write(TEXT);
        }

        assertEquals(ShiftCipher.encrypt(TEXT, KEY), target.toString());
    }

    @Test
    void encryptingWriterEncryptsSingleCharsAndArrayParts() throws IOException {
        StringWriter target = new StringWriter();
        try (Writer writer = new EncryptingWriter(target, KEY)) {
            writer.write('Я');
            writer.write("абвгд".toCharArray(), 1, 3); // "бвг"
            writer.write("Java", 0, 2);                // "Ja"
        }

        assertEquals(ShiftCipher.encrypt("ЯбвгJa", KEY), target.toString());
    }

    @Test
    void decryptingReaderRestoresTextByCharsAndByArray() throws IOException {
        String encrypted = ShiftCipher.encrypt("Аб" + TEXT, KEY);

        try (Reader reader = new DecryptingReader(new StringReader(encrypted), KEY)) {
            assertEquals('А', reader.read());
            assertEquals('б', reader.read());

            char[] buffer = new char[TEXT.length() + 10];
            int count = reader.read(buffer, 5, TEXT.length());

            assertEquals(TEXT.length(), count);
            assertEquals(TEXT, new String(buffer, 5, count));
            assertEquals(-1, reader.read());
        }
    }

    @Test
    void fileRoundTripThroughFilterStreams(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("cipher.txt");

        try (Writer writer = new EncryptingWriter(
                new OutputStreamWriter(new FileOutputStream(file.toFile()), StandardCharsets.UTF_8), KEY)) {
            writer.write(TEXT);
        }
        String onDisk = Files.readString(file, StandardCharsets.UTF_8);

        StringBuilder decrypted = new StringBuilder();
        try (Reader reader = new DecryptingReader(
                new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8), KEY)) {
            char[] buffer = new char[8];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                decrypted.append(buffer, 0, count);
            }
        }

        assertNotEquals(TEXT, onDisk);
        assertEquals(ShiftCipher.encrypt(TEXT, KEY), onDisk);
        assertEquals(TEXT, decrypted.toString());
    }
}
