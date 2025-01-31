package faang.school.postservice.validator.post;

import faang.school.postservice.client.SpellerClient;
import faang.school.postservice.dto.grammar.SpellResultDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentValidatorTest {
    @Mock
    private SpellerClient spellerClient;
    @InjectMocks
    private ContentValidator contentValidator;

    @Test
    public void shouldNotModifyContentWhenNoCorrectionsAreNeeded() {
        String originalContent = "test content";
        Post post = getPost(originalContent);
        Map<String, String> formData = Map.of("text", originalContent);
        when(spellerClient.checkText(formData)).thenReturn(new ArrayList<>());

        contentValidator.processPost(post);

        verify(spellerClient).checkText(formData);
        assertEquals(originalContent, post.getContent());
    }

    @Test
    public void shouldCorrectContentWhenErrorsAreFound() {
        String originalContent = "youre";
        String correctedContent = "you're";
        SpellResultDto spellResultDto = getSpellResultDto(0, 5, correctedContent);
        Post post = getPost(originalContent);
        Map<String, String> formData = Map.of("text", originalContent);
        when(spellerClient.checkText(formData)).thenReturn(List.of(spellResultDto));

        contentValidator.processPost(post);

        verify(spellerClient).checkText(formData);
        assertEquals(correctedContent, post.getContent());
    }

    private static SpellResultDto getSpellResultDto(int position, int oldLength, String correctedText) {
        return SpellResultDto.builder()
                .position(position)
                .oldLength(oldLength)
                .suggestions(new String[]{correctedText})
                .build();
    }

    private static Post getPost(String originalContent) {
        return Post.builder()
                .content(originalContent)
                .build();
    }
}