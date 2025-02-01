package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.producer.KafkaLikeProducer;
import faang.school.postservice.config.kafka.producer.KafkaPostProducer;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostCacheMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.cache.PostCache;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceCache {
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final PostRepository postRepository;
    private final PostCacheMapper mapper;

    public void savePost(Post post) {
        PostCache postCache = mapper.toCache(post);
        redisPostRepository.save(postCache);
        log.debug("Post added to Redis cache");
        List<Long> followersId = postRepository.findFollowersByAuthorId(post.getAuthorId());
        postCache.setFollowersId(followersId);
        UserDto author = userServiceClient.getUser(post.getAuthorId());
        redisUserRepository.save(new UserCache(author.getId(), author.getUsername()));
        log.debug("Author with id {} of the post added to Redis cache", author.getId());
        kafkaPostProducer.send(postCache);
        log.debug("Post with id {} added to Kafka topic", postCache.getId());
    }

    public void saveViewedPost(Post post) {

    }

    public void saveLike(Post post) {

    }
}
