package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@Slf4j
public class ModerationDictionary {
    private Set<String> forbiddenWords;

    @PostConstruct
    public void init() {
        forbiddenWords = new HashSet<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("moderation-words/bad-words.txt")) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource 'moderation-words/bad-words.txt' not found in the classpath");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                forbiddenWords = Collections.unmodifiableSet(reader.lines() .map(String::toLowerCase).collect(toSet()));
                log.info("Loaded forbidden words: {}", forbiddenWords);
            }

        } catch (IOException e) {
            log.error("Error reading file", e);
            throw new IllegalStateException("Failed to initialize forbidden words", e);
        }
    }

    public boolean containsForbiddenWord(String text) {
        String normalizedText = text.toLowerCase();
        for (String word : forbiddenWords) {
            if (normalizedText.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
