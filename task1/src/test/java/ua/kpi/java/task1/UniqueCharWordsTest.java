package ua.kpi.java.task1;

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
        assertFalse(UniqueCharWords.hasOnlyUniqueChars("Ананас"));
        assertFalse(UniqueCharWords.hasOnlyUniqueChars("Aa"));
        assertTrue(UniqueCharWords.hasOnlyUniqueChars("Kyiv"));
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
        assertTrue(UniqueCharWords.hasOnlyUniqueChars("п'ять"));
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
    void singleCharWordHasUniqueChars() {
        assertArrayEquals(new String[]{"I", "a"}, UniqueCharWords.find("I a"));
    }

    @Test
    void nullInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> UniqueCharWords.find(null));
    }
}
