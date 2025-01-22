package faang.school.postservice.service;

import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisPostRepository redisPostRepository;
    private final CachePostMapper cachePostMapper;

    public void publishPostAtRedis(Post post) {
        RedisPost redisPost = cachePostMapper.toCache(post);
        redisPostRepository.save(redisPost);
    }

    public void updatePostAtRedis(Post post) {
        Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(post.getId());

        if (optionalRedisPost.isPresent()) {
            RedisPost redisPost = optionalRedisPost.get();
            cachePostMapper.update(post, redisPost);
            redisPostRepository.save(redisPost);
        }
    }

    public void deletePostFromRedis(long postId){
        redisPostRepository.deleteById(postId);
    }
}
