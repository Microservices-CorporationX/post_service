package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisPostRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void savePost(PostDto post) {
        redisTemplate.opsForValue().set("post:" + post.getId(), post);
    }

    public PostDto getPost(Long postId) {
        return (PostDto) redisTemplate.opsForValue().get("post:" + postId);
    }
}
