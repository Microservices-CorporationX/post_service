package faang.school.postservice.publisher.user;

import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBanPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;

    @Override
    public void publish(Object object) {
        redisTemplate.convertAndSend(userBanTopic.getTopic(), redisTemplate);
    }
}
