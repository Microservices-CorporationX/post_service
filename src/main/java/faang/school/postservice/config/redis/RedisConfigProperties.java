package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisConfigProperties(String host, int port, Channel channel) {
    public record Channel(String ad_bought_channel, String calculations_channel, String user_bans) {
    }
}