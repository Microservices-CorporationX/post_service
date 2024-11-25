package faang.school.postservice.service.feed;

import faang.school.postservice.dto.redis.PostRedis;

import java.util.Set;

public interface FeedService {
    Set<PostRedis> getPosts(Long start, Long end, Long userId);
}
