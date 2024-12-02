package faang.school.postservice.config.redis.puplisher;

import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@EnableRedisRepositories(keyspaceConfiguration = RedisPublisherConfig.CustomKeyspaceConfiguration.class)
public class RedisPublisherConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.ttl.posts}")
    public long ttlPosts;

    @Value("${spring.data.redis.ttl.users}")
    public long ttlUsers;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       StringRedisSerializer stringRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(false);
        template.setDefaultSerializer(stringRedisSerializer);
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    public class CustomKeyspaceConfiguration extends KeyspaceConfiguration {

        @Override
        public Iterable<KeyspaceSettings> initialConfiguration() {
            KeyspaceSettings keyspacePostSettings = new KeyspaceSettings(PostRedis.class, "Posts");
            KeyspaceSettings keyspaceUserSettings = new KeyspaceSettings(UserRedis.class, "Users");
            keyspaceUserSettings.setTimeToLive(ttlUsers);
            keyspacePostSettings.setTimeToLive(ttlPosts);
            return List.of(keyspacePostSettings, keyspaceUserSettings);
        }
    }
}