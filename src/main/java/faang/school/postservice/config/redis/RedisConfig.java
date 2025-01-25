package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.publisher.MessageSenderForUserBanImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                redisProperties.getHost(), redisProperties.getPort()
        );
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ChannelTopic channelTopicForUserBan() {
        String topic = redisProperties.getUserBanTopic();
        log.info("Creating ChannelTopic for User Ban with topic: {}", topic);
        return new ChannelTopic(topic);
    }

    @Bean
    public ChannelTopic channelTopicForLikeAnalytics() {
        String topic = redisProperties.getLikeAnalyticsTopic();
        log.info("Creating ChannelTopic for Like Analytics with topic: {}", topic);
        return new ChannelTopic(topic);
    }

    @Bean
    public MessageSenderForUserBanImpl messageSenderForUserBan(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopicForUserBan) {
        return new MessageSenderForUserBanImpl(redisTemplate, channelTopicForUserBan);
    }

    @Bean
    public ChannelTopic adBoughtEventTopic() {
        String topic = redisProperties.getAdBoughtEvent();
        return new ChannelTopic(topic);
    }
}