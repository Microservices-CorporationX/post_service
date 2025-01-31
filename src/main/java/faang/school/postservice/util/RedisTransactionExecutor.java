package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class RedisTransactionExecutor {
    @Retryable(retryFor = {ConcurrentModificationException.class},
            maxAttemptsExpression = "#{@environment.getProperty('spring.data.redis.transaction-executor.retries')}",
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void executeRedisTransaction(RedisTemplate<String, Object> redisTemplate, String redisKey,
                                        Consumer<RedisOperations<String, Object>> consumer) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.watch(redisKey);
                operations.multi();

                consumer.accept(operations);
                List<Object> execResult = operations.exec();

                if (execResult.isEmpty()) {
                    log.error("""
                            Redis parallel transaction conflict.
                            Transaction result: {}
                            """, execResult);
                    throw new ConcurrentModificationException();
                }
                return execResult;
            }
        });
    }

    @Recover
    public void recover(ConcurrentModificationException e, RedisTemplate<String, Object> redisTemplate,
                        String redisKey, Consumer<RedisOperations<String, Object>> consumer) {
        log.error("All attempts failed for key: {}", redisKey, e);
    }
}
