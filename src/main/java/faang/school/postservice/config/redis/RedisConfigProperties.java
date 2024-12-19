package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisConfigProperties(String host, int port, Channel channel) {
    public record Channel(String ad_bought, String calculations, String like_events, String user_bans, String comments_events) {
    }
}