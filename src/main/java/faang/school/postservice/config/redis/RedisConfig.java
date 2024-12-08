package faang.school.postservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public RedisTemplate<String, ?> redisTemplateWithJson(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, new GenericJackson2JsonRedisSerializer());
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplateWithString(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, new GenericToStringSerializer<>(Long.class));
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(
            RedisConnectionFactory connectionFactory,
            org.springframework.data.redis.serializer.RedisSerializer<T> valueSerializer) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(valueSerializer);
        return redisTemplate;
    }
}
