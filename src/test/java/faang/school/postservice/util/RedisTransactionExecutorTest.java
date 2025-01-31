package faang.school.postservice.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisTransactionExecutorTest {
    private final RedisTransactionExecutor redisTransactionExecutor = new RedisTransactionExecutor();

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RedisOperations<String, Object> redisOperations;

    @Test
    public void testExecuteRedisTransaction() {
        ArgumentCaptor<SessionCallback<List<Object>>> sessionCallbackCaptor =
                ArgumentCaptor.forClass(SessionCallback.class);
        when(redisOperations.exec()).thenReturn(List.of(new Object()));

        doAnswer(invocationOnMock -> sessionCallbackCaptor.getValue().execute(redisOperations))
                .when(redisTemplate).execute(sessionCallbackCaptor.capture());

        redisTransactionExecutor.executeRedisTransaction(redisTemplate, "redis key", mock(Consumer.class));
        verify(redisOperations).exec();
    }

    @Test
    public void testExecuteRedisTransactionThrowsException() {
        ArgumentCaptor<SessionCallback<List<Object>>> sessionCallbackCaptor =
                ArgumentCaptor.forClass(SessionCallback.class);
        when(redisTemplate.execute(sessionCallbackCaptor.capture()))
                .thenAnswer(invocationOnMock -> {
                    sessionCallbackCaptor.getValue().execute(redisOperations);
                    return null;
                });

        assertThrows(ConcurrentModificationException.class,
                () -> redisTransactionExecutor
                        .executeRedisTransaction(redisTemplate, "redis key", mock(Consumer.class)));
    }
}