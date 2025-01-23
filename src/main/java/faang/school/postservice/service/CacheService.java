package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.CacheAuthor;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisAuthor;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.repository.RedisAuthorRepository;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisPostRepository redisPostRepository;
    private final CachePostMapper cachePostMapper;
    private final RedisAuthorRepository redisAuthorRepository;
    private final UserServiceClient userServiceClient;

    public void publishPostAndAuthorAtRedis(Post post) {
        publishPostAuthor(post);
        publishPostAtRedis(post);
    }

    public void publishPostAtRedis(Post post) {
        RedisPost redisPost = cachePostMapper.toCache(post);
        redisPostRepository.save(redisPost);
    }

    public void publishPostAuthor(Post post) {
        RedisAuthor redisAuthor = new RedisAuthor();
        redisAuthor.setId(post.getAuthorId());

        UserDto userDto = userServiceClient.getUserById(post.getAuthorId());
        redisAuthor.setName(userDto.getUsername());

        redisAuthorRepository.save(redisAuthor);
    }

    public void publishCommentAuthor(Long authorId) {
        Optional<RedisAuthor> optionalRedisAuthor = redisAuthorRepository.findById("User: " + authorId);

        if (optionalRedisAuthor.isEmpty()) {
            RedisAuthor redisAuthor = new RedisAuthor();
            redisAuthor.setId(authorId);
            UserDto userDto = userServiceClient.getUserById(authorId);
            redisAuthor.setName(userDto.getUsername());
        }
    }

    public void updatePostAtRedis(Post post) {
        Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(post.getId());

        if (optionalRedisPost.isPresent()) {
            RedisPost redisPost = optionalRedisPost.get();
            cachePostMapper.update(post, redisPost);
            redisPostRepository.save(redisPost);
        }
    }

    public void deletePostFromRedis(long postId) {
        redisPostRepository.deleteById(postId);
    }
}
