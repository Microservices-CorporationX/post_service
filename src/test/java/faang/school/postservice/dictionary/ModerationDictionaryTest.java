package faang.school.postservice.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ModerationDictionaryTest {
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    public void setUp() {
        moderationDictionary = new ModerationDictionary();
        moderationDictionary.init();
    }

    @Test
    void testForbiddenWordsLoading() {
        assertNotNull(moderationDictionary, "ModerationDictionary instance should not be null");

        String testWord = "bitch";
        assertTrue(moderationDictionary.containsForbiddenWord(testWord),
                "ModerationDictionary should detect forbidden words from the list");

        assertFalse(moderationDictionary.containsForbiddenWord(""),
                "Empty string should not be detected as a forbidden word");
    }
}
