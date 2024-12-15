package faang.school.postservice.redis.publisher;

import faang.school.postservice.dto.like.LikePostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher {

    @Value("${spring.data.redis.channel.like-post}")
    private String userBanTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikePostResponseDto likePostResponseDto) {
        log.info("Publishing like event for post ID: {}", likePostResponseDto.getPostId());
        redisTemplate.convertAndSend(userBanTopic, likePostResponseDto);
    }
}