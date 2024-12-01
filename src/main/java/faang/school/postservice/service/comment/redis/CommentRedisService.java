package faang.school.postservice.service.comment.redis;

import faang.school.postservice.model.comment.CommentRedis;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CommentRedisService {
    private final static String COMMENT_REDIS_KEY = "comment:";

    private final CommentRedisRepository commentRedisRepository;
    private final RedisTemplate<String, Object> commonRedisTemplate;

    public void saveCommentToRedis(Long commentId, String content, long authorId) {
        CommentRedis commentRedis = new CommentRedis(commentId, content, 0, authorId);
        commentRedisRepository.save(commentRedis);
    }

    public void changeLikesAmountForComments(Map<Long, Integer> postLikes) {
        for (Map.Entry<Long, Integer> postLike : postLikes.entrySet()) {
            String key = buildKey(postLike.getKey());
            commonRedisTemplate.opsForHash().increment(key, "likes", postLike.getValue());
        }
    }

    private String buildKey(Long postId) {
        return COMMENT_REDIS_KEY + postId;
    }
}
