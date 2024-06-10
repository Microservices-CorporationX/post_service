package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventPublisher extends AbstractEventPublisher<PostViewEvent> {

    private final ChannelTopic postViewTopic;

    public PostViewEventPublisher(RedisTemplate redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Qualifier("postViewChannel") ChannelTopic postViewTopic) {

        super(redisTemplate, objectMapper);
        this.postViewTopic = postViewTopic;
    }

    public void sandEvent(PostViewEvent event) {
        publish(postViewTopic, event);
    }
}
