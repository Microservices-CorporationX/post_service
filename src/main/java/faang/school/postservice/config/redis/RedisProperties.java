package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private Channels channels;
    private String host;
    private int port;
    private int maxPostCountInFeed;
    private int postTtl;
    private int defaultTtl;
    private int authorTtl;
    private int commentTtl;
    private String postPrefix;
    private String commentPrefix;
    private String redissonAddress;

    @Getter
    @Setter
    protected static class Channels {
        private Channel calculationsChannel;
        private Channel likePostChannel;
        private Channel newCommentChannel;
        private Channel commentChannel;
        private Channel postViewChannel;
        private Channel userBanChannel;

        @Getter
        @Setter
        protected static class Channel {
            private String name;
        }
    }
}