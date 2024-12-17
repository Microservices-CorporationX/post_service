package faang.school.postservice.config.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisTopics {

    @Value("${spring.data.redis.channels.ban_user_topic.name}")
    private String banUserTopic;
    @Bean
    public ChannelTopic banUserTopic(){
        return new ChannelTopic(banUserTopic);
    }
}
