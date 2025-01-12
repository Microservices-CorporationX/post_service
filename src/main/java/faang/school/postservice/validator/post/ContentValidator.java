package faang.school.postservice.validator.post;

import faang.school.postservice.client.SpellerClient;
import faang.school.postservice.dto.grammar.SpellResultDto;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentValidator {
    private final SpellerClient spellerClient;

    public void processPost(Post post) {
        try {
            String correctedContent = getCorrectedContent(post);
            post.setContent(correctedContent);
        } catch (FeignException e) {
            log.error("Exception in speller client: {}", e.getCause(), e);
            throw e;
        }
    }

    private String getCorrectedContent(Post post) {
        String originalText = post.getContent();
        Map<String, String> formData = Map.of("text", originalText);

        List<SpellResultDto> spellResults = spellerClient.checkText(formData);
        if (spellResults.isEmpty()) {
            return originalText;
        }
        return applyCorrections(originalText, spellResults);
    }

    private static String applyCorrections(String originalText, List<SpellResultDto> spellResults) {
        StringBuilder correctedText = new StringBuilder(originalText);
        int offset = 0;
        for (SpellResultDto result : spellResults) {
            String[] suggestions = result.getSuggestions();
            if (suggestions == null || suggestions.length == 0) {
                continue;
            }

            String replacement = suggestions[0];
            int start = result.getPosition() + offset;
            int end = start + result.getOldLength();

            correctedText.replace(start, end, replacement);
            offset += replacement.length() - result.getOldLength();
        }
        return correctedText.toString();
    }
}