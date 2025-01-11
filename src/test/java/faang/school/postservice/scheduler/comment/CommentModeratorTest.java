package faang.school.postservice.scheduler.comment;

import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentModeratorTest {
    @Mock
    CommentService commentService;
    @InjectMocks
    CommentModerator commentModerator;

    @Test
    void testModerateCommentsToOffensiveContent() {
        commentModerator.moderateCommentsToOffensiveContent();
        verify(commentService).checkProfanities();
    }
}