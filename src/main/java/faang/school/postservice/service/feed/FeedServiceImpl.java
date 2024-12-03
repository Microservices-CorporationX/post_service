package faang.school.postservice.service.feed;

import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.repository.feed.FeedRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRedisRepository redisRepository;
    @Override
    public List<PostRedis> getPosts(Long start, Long end, Long userId) {
        Set<Object> posts = redisRepository.getAllFeedAsc(userId);
        List<PostRedis> postRedisList = posts.stream().map(o -> (PostRedis) o).toList();
        return postRedisList;
    }
}
