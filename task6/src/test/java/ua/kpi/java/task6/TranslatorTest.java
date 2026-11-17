package ua.kpi.java.task6;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тести класу {@link Translator}.
 */
class TranslatorTest {

    private Translator translator;

    @BeforeEach
    void setUp() {
        translator = new Translator();
        translator.addWord("hello", "привіт");
        translator.addWord("world", "світ");
        translator.addWord("my", "мій");
        translator.addWord("friend", "друг");
    }

    @Test
    void translatesKnownWord() {
        assertEquals("привіт", translator.translate("hello"));
    }

    @Test
    void translatesPhraseWordByWord() {
        assertEquals("привіт мій друг", translator.translate("hello my friend"));
    }

    @Test
    void lookupIsCaseInsensitive() {
        assertEquals("світ", translator.translate("world"));
        assertEquals("Світ", translator.translate("WORLD"));
        assertEquals("світ", translator.translate("wORLD"));
    }

    @Test
    void capitalizedWordGetsCapitalizedTranslation() {
        assertEquals("Привіт, світ", translator.translate("Hello, world"));
    }

    @Test
    void punctuationSpacesAndDigitsAreKept() {
        assertEquals("Привіт, світ!", translator.translate("Hello, world!"));
        assertEquals("привіт  2 світ?!", translator.translate("hello  2 world?!"));
    }

    @Test
    void unknownWordsAreLeftAsIsAndMarked() {
        assertEquals("Привіт, [Java]!", translator.translate("Hello, Java!"));
    }

    @Test
    void findUnknownWordsReturnsEachUnknownWordOnce() {
        List<String> unknown = translator.findUnknownWords("Hello Java, hello Kotlin, Java!");

        assertEquals(List.of("Java", "Kotlin"), unknown);
    }

    @Test
    void findUnknownWordsIsEmptyWhenAllWordsAreKnown() {
        assertTrue(translator.findUnknownWords("Hello, my friend!").isEmpty());
    }

    @Test
    void addedPairIsUsedForTranslation() {
        String previous = translator.addWord("Coffee", "кава");

        assertNull(previous);
        assertEquals(5, translator.size());
        assertEquals("кава", translator.getDictionary().get("coffee"));
        assertEquals("мій кава", translator.translate("my coffee"));
    }

    @Test
    void addingExistingWordReplacesTranslation() {
        String previous = translator.addWord("friend", "товариш");

        assertEquals("друг", previous);
        assertEquals(4, translator.size());
        assertEquals("мій товариш", translator.translate("my friend"));
    }

    @Test
    void translationMayContainApostropheHyphenAndSpace() {
        translator.addWord("family", "сім'я");
        translator.addWord("goodbye", "до побачення");
        translator.addWord("somebody", "хтось-то");

        assertEquals("мій сім'я", translator.translate("my family"));
        assertEquals("До побачення!", translator.translate("Goodbye!"));
        assertEquals("хтось-то", translator.translate("somebody"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "hello1", "two words", "e-mail", "привіт"})
    void invalidEnglishWordIsRejected(String english) {
        assertThrows(IllegalArgumentException.class, () -> translator.addWord(english, "слово"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "privit", "123", "'кава", "кава1"})
    void invalidUkrainianTranslationIsRejected(String ukrainian) {
        assertThrows(IllegalArgumentException.class, () -> translator.addWord("coffee", ukrainian));
    }

    @Test
    void rejectedPairIsNotAdded() {
        assertThrows(IllegalArgumentException.class, () -> translator.addWord("coffee", "coffee"));

        assertEquals(4, translator.size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "12345", "!!!", "привіт світ"})
    void phraseWithoutEnglishWordsIsRejected(String phrase) {
        assertThrows(IllegalArgumentException.class, () -> translator.translate(phrase));
    }

    @Test
    void dictionaryCannotBeChangedFromOutside() {
        assertThrows(UnsupportedOperationException.class,
                () -> translator.getDictionary().put("cat", "кіт"));
    }
}
