package faang.school.postservice.service.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.post.PostRedisEntity;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentRedisServiceTest {
    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 2L;
    private static final Long POST_AUTHOR_ID = 3L;
    private static final Long COMMENT_AUTHOR_ID = 4L;
    private static final String CONTENT = "test content";
    private static final String REDIS_KEY = "test key";
    private static final int COMMENTS_NUMBER = 3;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private TransactionTemplate transactionTemplate;
    @InjectMocks
    private CommentRedisService commentRedisService;

    @Mock
    private RedisOperations<String, Object> redisOperations;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    public void setUp() {
        when(redisOperations.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testUpdatePostInRedis() throws JsonProcessingException {
        CommentEvent commentEvent = getCommentEvent();
        PostRedisEntity postRedisEntity = getPostRedisEntity();
        String cachedPost = "cachedPost";
        String updatedPost = "updatedPost";

        when(objectMapper.readValue(cachedPost, PostRedisEntity.class)).thenReturn(postRedisEntity);
        when(objectMapper.writeValueAsString(postRedisEntity)).thenReturn(updatedPost);

        commentRedisService.updatePostInRedis(REDIS_KEY, commentEvent, cachedPost, redisOperations);
        verify(valueOperations).set(REDIS_KEY, updatedPost);
    }

    @Test
    public void testCreatePostInRedis() throws JsonProcessingException {
        CommentEvent commentEvent = getCommentEvent();
        PostRedisEntity postRedisEntity = getPostRedisEntity();
        String createdPost = "createdPost";
        Comment comment = new Comment();

        when(postRepository.findPostAsRedisEntityById(POST_ID)).thenReturn(Optional.of(postRedisEntity));
        when(objectMapper.writeValueAsString(postRedisEntity)).thenReturn(createdPost);
        when(commentRepository.findLatestByPostId(eq(commentEvent.getPostId()), anyInt()))
                .thenReturn(List.of(comment));
        when(commentMapper.toEvent(comment)).thenReturn(commentEvent);

        ArgumentCaptor<Consumer<TransactionStatus>> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        doAnswer(invocationOnMock -> {
            consumerCaptor.getValue().accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(consumerCaptor.capture());

        commentRedisService.createPostInRedis(REDIS_KEY, commentEvent, redisOperations);
        assertEquals(1, postRedisEntity.getComments().size());
        verify(valueOperations).set(REDIS_KEY, createdPost);
    }

    private CommentEvent getCommentEvent() {
        return new CommentEvent(COMMENT_ID,
                COMMENT_AUTHOR_ID,
                POST_ID,
                CONTENT,
                LocalDateTime.now());
    }

    private PostRedisEntity getPostRedisEntity() {
        TreeSet<CommentEvent> comments = new TreeSet<>();
        for (int i = 0; i < COMMENTS_NUMBER; i++) {
            comments.add(new CommentEvent(COMMENT_ID, COMMENT_AUTHOR_ID, POST_ID, CONTENT, LocalDateTime.now()));
        }

        return new PostRedisEntity(POST_ID,
                POST_AUTHOR_ID,
                comments,
                LocalDateTime.now());
    }
}

