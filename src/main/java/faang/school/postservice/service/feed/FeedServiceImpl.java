package faang.school.postservice.service.feed;

import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.repository.feed.FeedRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRedisRepository redisRepository;
    @Override
    public Set<PostRedis> getPosts(Long start, Long end, Long userId) {
        Set<Object> posts = redisRepository.getAllFeedAsc(userId);
        return Set.of();
    }
}
