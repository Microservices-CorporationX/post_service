package faang.school.postservice.scheduler.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostCorrecterSchedulerTest {
    @Mock
    private PostService postService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PostCorrecterScheduler postCorrecterScheduler;

    @Test
    public void testCorrectContentOfUnpublishedPosts() {
        postCorrecterScheduler.correctContentOfUnpublishedPosts();

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userContext).setUserId(userIdCaptor.capture());
        verify(postService).checkText();
        assertEquals(1L, userIdCaptor.getValue());
    }
}