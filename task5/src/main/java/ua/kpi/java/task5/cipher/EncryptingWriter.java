package ua.kpi.java.task5.cipher;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Фільтр символьного потоку виведення: шифрує кожен символ ({@link ShiftCipher#encrypt(char, char)})
 * перед тим, як передати його далі в обгорнутий потік.
 */
public class EncryptingWriter extends FilterWriter {

    private final char key;

    /**
     * @param out потік, у який записується зашифрований текст
     * @param key ключовий символ
     */
    public EncryptingWriter(Writer out, char key) {
        super(out);
        this.key = key;
    }

    /** Шифрує і записує один символ. */
    @Override
    public void write(int c) throws IOException {
        out.write(ShiftCipher.encrypt((char) c, key));
    }

    /** Шифрує і записує частину масиву символів. */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] encrypted = new char[len];
        for (int i = 0; i < len; i++) {
            encrypted[i] = ShiftCipher.encrypt(cbuf[off + i], key);
        }
        out.write(encrypted, 0, len);
    }

    /** Шифрує і записує частину рядка. */
    @Override
    public void write(String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }
}
