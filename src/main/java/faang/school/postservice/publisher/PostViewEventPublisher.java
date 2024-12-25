package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostViewEventPublisher implements MessagePublisher<PostViewEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewTopic;

    @Override
    public void publish(PostViewEvent message) {
        redisTemplate.convertAndSend(postViewTopic.getTopic(), message);
        log.info("Message: {}, was published in topic - {}", message, postViewTopic.getTopic());
    }
}
