package ru.corporationx.postservice.scheduler.post;

import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.repository.PostRepository;
import ru.corporationx.postservice.service.post.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.corporationx.postservice.scheduler.post.ModerationScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationSchedulerTest {

    @InjectMocks
    private ModerationScheduler moderationScheduler;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    private int moderationBatchSize = 50;

    private List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setup() {
        posts.add(Post.builder()
                .id(1L)
                .content("any content")
                .build());
        ReflectionTestUtils.setField(moderationScheduler, "moderationBachSize", moderationBatchSize);
    }

    @Test
    public void testToPostContentToOffensive_ShouldSuccessProcessed() {
        when(postRepository.findNotCheckedToVerificationPosts(any()))
                .thenReturn(Optional.of(posts));
        mockAllPostsAsProfane(posts);

        moderationScheduler.moderatePostToOffensiveContent();

        verify(postRepository, times(1))
                .findNotCheckedToVerificationPosts(any());
        checkToChangingPostFields(posts);
    }

    @Test
    public void testToPostContentToOffensive_DontSuccessProcessed() {
        when(postRepository.findNotCheckedToVerificationPosts(any()))
                .thenReturn(Optional.empty());

        moderationScheduler.moderatePostToOffensiveContent();

        verify(moderationDictionary, times(0)).containsProfanity(Mockito.anyString());
    }

    @Test
    public void testToCheckOffensive_ShouldSuccessUpdatePostFields() {
        for (Post post : posts) {
            when(moderationDictionary.containsProfanity(post.getContent()))
                    .thenReturn(true);
        }

        moderationScheduler.checkToOffensive(posts).join();

        checkToChangingPostFields(posts);
    }

    private void checkToChangingPostFields(List<Post> posts) {
        for (Post post : posts) {
            verify(moderationDictionary, times(1))
                    .containsProfanity(post.getContent());
            assertNotNull(post.getContent());
            assertFalse(post.getVerified());
        }
    }

    public void mockAllPostsAsProfane(List<Post> posts) {
        for (Post post : posts) {
            when(moderationDictionary.containsProfanity(post.getContent()))
                    .thenReturn(true);
        }
    }
}
