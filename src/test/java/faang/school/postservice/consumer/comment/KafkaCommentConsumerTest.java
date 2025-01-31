package faang.school.postservice.consumer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.CommentRedisService;
import faang.school.postservice.util.RedisTransactionExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.support.Acknowledgment;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentConsumerTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CommentRedisService commentRedisService;
    @Mock
    private RedisTransactionExecutor redisTransactionExecutor;
    @InjectMocks
    private KafkaCommentConsumer kafkaCommentConsumer;

    @Test
    public void testConsume_WhenCommentExist() throws JsonProcessingException {
        String message = "test message";
        CommentEvent newComment = getCommentEvent();
        Acknowledgment ack = mock(Acknowledgment.class);

        when(objectMapper.readValue(message, CommentEvent.class)).thenReturn(newComment);
        when(commentRepository.existsById(newComment.getCommentId())).thenReturn(true);

        kafkaCommentConsumer.consume(message, ack);

        verify(redisTransactionExecutor).executeRedisTransaction(eq(redisTemplate), anyString(), any(Consumer.class));
        verify(ack).acknowledge();
    }

    @Test
    public void testLambda_WhenCachedPostIsNotNull() throws JsonProcessingException {
        String message = "test message";
        String cachedPost = "not null";
        CommentEvent newComment = getCommentEvent();
        Acknowledgment ack = mock(Acknowledgment.class);

        when(objectMapper.readValue(message, CommentEvent.class)).thenReturn(newComment);
        when(commentRepository.existsById(newComment.getCommentId())).thenReturn(true);

        RedisOperations<String, Object> mockRedisOperations = mock(RedisOperations.class);
        ValueOperations<String, Object> mockValueOperations = mock(ValueOperations.class);
        when(mockRedisOperations.opsForValue()).thenReturn(mockValueOperations);
        when(mockValueOperations.get(anyString())).thenReturn(cachedPost);

        ArgumentCaptor<Consumer<RedisOperations<String, Object>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);
        doAnswer(invocationOnMock -> {
            consumerCaptor.getValue().accept(mockRedisOperations);
            return null;
        }).when(redisTransactionExecutor)
                .executeRedisTransaction(eq(redisTemplate), anyString(), consumerCaptor.capture());

        kafkaCommentConsumer.consume(message, ack);
        verify(commentRedisService).updatePostInRedis(anyString(), eq(newComment),
                eq(cachedPost), eq(mockRedisOperations));
    }

    @Test
    public void testLambda_WhenCachedPostIsNull() throws JsonProcessingException {
        String message = "test message";
        CommentEvent newComment = getCommentEvent();
        Acknowledgment ack = mock(Acknowledgment.class);

        when(objectMapper.readValue(message, CommentEvent.class)).thenReturn(newComment);
        when(commentRepository.existsById(newComment.getCommentId())).thenReturn(true);

        RedisOperations<String, Object> mockRedisOperations = mock(RedisOperations.class);
        ValueOperations<String, Object> mockValueOperations = mock(ValueOperations.class);
        when(mockRedisOperations.opsForValue()).thenReturn(mockValueOperations);

        ArgumentCaptor<Consumer<RedisOperations<String, Object>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);
        doAnswer(invocationOnMock -> {
            consumerCaptor.getValue().accept(mockRedisOperations);
            return null;
        }).when(redisTransactionExecutor)
                .executeRedisTransaction(eq(redisTemplate), anyString(), consumerCaptor.capture());

        kafkaCommentConsumer.consume(message, ack);
        verify(commentRedisService).createPostInRedis(anyString(), eq(newComment), eq(mockRedisOperations));
    }

    @Test
    public void testConsumeWhenCommentDoesntExist() throws JsonProcessingException {
        String message = "test message";
        CommentEvent newComment = getCommentEvent();
        Acknowledgment ack = mock(Acknowledgment.class);
        when(objectMapper.readValue(message, CommentEvent.class)).thenReturn(newComment);

        kafkaCommentConsumer.consume(message, ack);
        verify(ack).acknowledge();
        verifyNoMoreInteractions(ack);
    }

    @Test
    public void testConsumeJsonParseDropsException() throws JsonProcessingException {
        String message = "test message";
        Acknowledgment ack = mock(Acknowledgment.class);
        when(objectMapper.readValue(message, CommentEvent.class)).thenThrow(mock(JsonProcessingException.class));

        assertThrows(RuntimeException.class, () -> kafkaCommentConsumer.consume(message, ack));
    }

    private static CommentEvent getCommentEvent() {
        return CommentEvent.builder()
                .commentId(1L)
                .postId(2L)
                .build();
    }
}