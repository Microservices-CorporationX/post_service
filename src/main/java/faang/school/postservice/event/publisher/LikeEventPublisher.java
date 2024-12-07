package faang.school.postservice.event.publisher;

import faang.school.postservice.event.model.LikeEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
