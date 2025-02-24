package ru.corporationx.postservice.scheduler.post;

import ru.corporationx.postservice.config.context.UserContext;
import ru.corporationx.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.postservice.scheduler.post.PostCorrecterScheduler;

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