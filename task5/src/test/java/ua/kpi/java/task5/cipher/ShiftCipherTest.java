package ua.kpi.java.task5.cipher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Тести методів шифрування й дешифрування {@link ShiftCipher}.
 */
class ShiftCipherTest {

    @Test
    void encryptAddsKeyCode() {
        // 'A' = 65, '\u0003' = 3, 65 + 3 = 68 = 'D'
        assertEquals('D', ShiftCipher.encrypt('A', '\u0003'));
        assertEquals((char) ('а' + 'К'), ShiftCipher.encrypt('а', 'К'));
    }

    @Test
    void decryptSubtractsKeyCode() {
        assertEquals('A', ShiftCipher.decrypt('D', '\u0003'));
    }

    @Test
    void wrapsAroundOnCharOverflow() {
        char maxChar = (char) 65535;
        char encrypted = ShiftCipher.encrypt(maxChar, 'B'); // (65535 + 66) mod 65536 = 65 = 'A'

        assertEquals('A', encrypted);
        assertEquals(maxChar, ShiftCipher.decrypt(encrypted, 'B'));
    }

    @Test
    void stringRoundTripRestoresUkrainianText() {
        String text = "Привіт, Java! Ґанок, їжак, є'ще — 2025.";
        char key = 'К';

        String encrypted = ShiftCipher.encrypt(text, key);

        assertNotEquals(text, encrypted);
        assertEquals(text.length(), encrypted.length());
        assertEquals(text, ShiftCipher.decrypt(encrypted, key));
    }
}
