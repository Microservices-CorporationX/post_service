package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

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
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
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
}
