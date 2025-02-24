package ru.corporationx.postservice.publisher.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.corporationx.postservice.publisher.AbstractEventPublisher;
import ru.corporationx.postservice.publisher.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserBanPublisher extends AbstractEventPublisher implements MessagePublisher {

    public UserBanPublisher(
            ObjectMapper objectMapper,
            RedisTemplate<String, Object> redisTemplate,
            ChannelTopic userBanTopic) {
        super(objectMapper, redisTemplate, userBanTopic);
    }

    @Override
    public void publish(Object object) {
        convertAndSend(object);
    }
}
