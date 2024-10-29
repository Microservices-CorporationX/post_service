package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
public class PostCacheService {
    @Value(value = "${spring.data.redis.post-cache.comments-in-post:3}")
    private int maxCommentsInPostCache;
    private final RedisPostRepository redisPostRepository;

    public List<RedisPost> getPostCacheByIds(List<Long> postIds) {
        return redisPostRepository.findAllById(postIds);
    }

    public void incrementPostLikes(Long postId) {
        RedisPost redisPost = getRedisPost(postId);
        redisPost.incrementNumLikes();
        redisPostRepository.save(redisPost);
    }

    public void addPostView(Long postId) {
        RedisPost redisPost = getRedisPost(postId);
        redisPost.incrementNumViews();
        redisPostRepository.save(redisPost);
    }

    public void addCommentToPostCache(Long postId, CommentDto commentDto) {
        RedisPost redisPost = getRedisPost(postId);
        CopyOnWriteArraySet<CommentDto> comments = redisPost.getComments();
        checkCapacity(comments);
        comments.add(commentDto);
    }

    private void checkCapacity(CopyOnWriteArraySet<CommentDto> comments) {
        if (comments.size() == maxCommentsInPostCache) {
            comments.stream().findFirst().ifPresent(comments::remove);
        }
    }

    private RedisPost getRedisPost(Long postId) {
        return redisPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("No post in Redis"));
    }
}
