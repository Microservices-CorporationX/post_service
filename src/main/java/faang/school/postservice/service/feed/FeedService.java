package faang.school.postservice.service.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    @Value("${spring.data.redis.key-hash-feeds}")
    private String keyFeed;

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public Mono<Void> bindPostToFollower(Long followerId, Long postId) {

    }
}
