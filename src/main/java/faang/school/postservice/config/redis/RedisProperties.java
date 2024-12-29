package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private Channels channels;

    @Setter
    @Getter
    protected static class Channels {
        private Channel userBanChannel;
        private Channel postViewChannel;

        @Setter
        @Getter
        protected static class Channel {
            private String name;
        }
    }
}
