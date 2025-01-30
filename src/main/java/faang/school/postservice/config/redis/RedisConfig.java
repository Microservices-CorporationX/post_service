package faang.school.postservice.config.redis;

import faang.school.postservice.cache_entities.AuthorCache;
import faang.school.postservice.cache_entities.NewsFeedCache;
import faang.school.postservice.cache_entities.PostCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableRedisRepositories(keyspaceConfiguration = RedisConfig.RedisKeyspaceConfig.class)
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${cache.post.ttl}")
    private long postCacheTtl;

    @Value("${cache.author.ttl}")
    private long authorCacheTtl;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    public class RedisKeyspaceConfig extends KeyspaceConfiguration {
        @Override
        protected Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings feedKeyspaceSettings = new KeyspaceSettings(NewsFeedCache.class, "Newsfeed");
            KeyspaceSettings authorKeyspaceSettings = new KeyspaceSettings(AuthorCache.class, "Author");
            authorKeyspaceSettings.setTimeToLive(TimeUnit.HOURS.toSeconds(authorCacheTtl));
            KeyspaceSettings postKeyspaceSettings = new KeyspaceSettings(PostCache.class, "Post");
            postKeyspaceSettings.setTimeToLive(TimeUnit.HOURS.toSeconds(postCacheTtl));
            return List.of(feedKeyspaceSettings, postKeyspaceSettings, authorKeyspaceSettings);
        }
    }
}