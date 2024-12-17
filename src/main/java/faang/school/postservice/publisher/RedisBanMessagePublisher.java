package faang.school.postservice.publisher;

import faang.school.postservice.events.BanUserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisBanMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    public void publish(BanUserEvent event) {
        log.info("sending message to redis");
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
