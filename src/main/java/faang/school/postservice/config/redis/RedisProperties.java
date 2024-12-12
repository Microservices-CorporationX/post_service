package faang.school.postservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private String host;
    private int port;
    private Channel channel;

    @Data
    public static class Channel {
        private String userBansChannel;
        private String commentChannel;
    }
}
