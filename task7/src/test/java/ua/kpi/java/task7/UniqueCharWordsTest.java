package ua.kpi.java.task7;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueCharWordsTest {

    @Test
    void findsWordsWithUniqueCharsInOrder() {
        assertArrayEquals(new String[]{"is", "a", "world"},
                UniqueCharWords.find("Java is a world apple"));
    }

    @Test
    void ignoresCaseWhenComparingChars() {
        assertFalse(UniqueCharWords.HAS_ONLY_UNIQUE_CHARS.test("Ананас"));
        assertFalse(UniqueCharWords.HAS_ONLY_UNIQUE_CHARS.test("Aa"));
        assertTrue(UniqueCharWords.HAS_ONLY_UNIQUE_CHARS.test("Kyiv"));
    }

    @Test
    void worksWithCyrillicWords() {
        assertArrayEquals(new String[]{"кіт", "і", "дворі"},
                UniqueCharWords.find("кіт і собака дворі"));
    }

    @Test
    void splitsByAnyWhitespaceAndTrimsPunctuation() {
        assertArrayEquals(new String[]{"one", "two", "three", "four"},
                UniqueCharWords.splitWords("  one,\ttwo\n\nthree!  (four)  "));
    }

    @Test
    void keepsApostropheAndHyphenInsideWord() {
        assertArrayEquals(new String[]{"п'ять", "e-mail"},
                UniqueCharWords.splitWords("п'ять e-mail"));
        assertTrue(UniqueCharWords.HAS_ONLY_UNIQUE_CHARS.test("п'ять"));
    }

    @Test
    void punctuationOnlyTokensAreNotWords() {
        assertArrayEquals(new String[]{"cat", "dog"}, UniqueCharWords.splitWords("cat - dog ..."));
    }

    @Test
    void returnsEmptyArrayWhenNothingFound() {
        assertEquals(0, UniqueCharWords.find("hello letter").length);
        assertEquals(0, UniqueCharWords.find("   ").length);
        assertEquals(0, UniqueCharWords.find("").length);
    }

    @Test
    void sameResultAsLoopVersionOnDemoText() {
        String text = "Java is a programming language, кіт і собака грають у дворі, world peace, Ананас";
        assertArrayEquals(new String[]{"is", "a", "кіт", "і", "грають", "у", "дворі", "world"},
                UniqueCharWords.find(text));
    }

    @Test
    void nullInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> UniqueCharWords.find(null));
    }
}
