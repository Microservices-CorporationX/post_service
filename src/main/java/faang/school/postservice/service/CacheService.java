package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.CacheAuthor;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisAuthor;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.repository.RedisAuthorRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.validator.CacheServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisPostRepository redisPostRepository;
    private final CachePostMapper cachePostMapper;
    private final RedisAuthorRepository redisAuthorRepository;
    private final UserServiceClient userServiceClient;

    private final CacheServiceValidator validator;

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

    public void addLikeToCommentOrPost(LikeEvent likeEvent) {
        validator.validateLikeEvent(likeEvent);

        Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(likeEvent.getPostId());
        if (optionalRedisPost.isPresent()) {

            RedisPost redisPost = optionalRedisPost.get();

            if (likeEvent.getCommentId() > 0) {
                for (CommentDto commentDto : redisPost.getCommentsDto()) {

                    if (commentDto.getId() == likeEvent.getCommentId()) {
                        commentDto.getLikeIds().add(likeEvent.getLikeId());
                        break;
                    }

                }

                redisPostRepository.save(redisPost);
            } else {
                redisPost.setLikes(redisPost.getLikes() + 1);
            }
        }


    }

    public void deletePostFromRedis(long postId) {
        redisPostRepository.deleteById(postId);
    }
}
