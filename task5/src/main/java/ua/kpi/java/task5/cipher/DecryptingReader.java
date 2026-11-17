package ua.kpi.java.task5.cipher;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Фільтр символьного потоку введення: дешифрує кожен прочитаний символ
 * ({@link ShiftCipher#decrypt(char, char)}).
 */
public class DecryptingReader extends FilterReader {

    private final char key;

    /**
     * @param in  потік із зашифрованим текстом
     * @param key ключовий символ
     */
    public DecryptingReader(Reader in, char key) {
        super(in);
        this.key = key;
    }

    /** Читає і дешифрує один символ; повертає -1 у кінці потоку. */
    @Override
    public int read() throws IOException {
        int c = in.read();
        return c == -1 ? -1 : ShiftCipher.decrypt((char) c, key);
    }

    /** Читає символи в масив і дешифрує їх; повертає кількість прочитаних символів або -1. */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int count = in.read(cbuf, off, len);
        for (int i = off; i < off + count; i++) {
            cbuf[i] = ShiftCipher.decrypt(cbuf[i], key);
        }
        return count;
    }
}
