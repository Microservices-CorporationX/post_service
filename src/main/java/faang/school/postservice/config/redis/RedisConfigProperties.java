package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisConfigProperties(String host, int port, Channels channels) {
    public record Channels(Channel ad_bought_channel, Channel calculations_channel, Channel user_bans_channel) {
    }

    public record Channel(String name) {
    }
}