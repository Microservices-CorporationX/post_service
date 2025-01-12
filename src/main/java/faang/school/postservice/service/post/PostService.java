package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.KafkaPostDto;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachePost;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.publisher.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachePostRepository;
import faang.school.postservice.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final PostDataPreparer preparer;
    private final List<PostFilter> postFilters;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaPostViewProducer kafkaPostViewProducer;
    private final UserServiceClient userClient;
    private final CachePostRepository cachePostRepository;
    private final UserCacheService userCacheService;
    private final RedissonClient redissonClient;
    @Value("${spring.data.redis.redisson_client.name_version}")
    private String version;
    @Value("${spring.data.redis.redisson_client.key_for_version}")
    private String versionedKey;
    @Value("${spring.data.redis.redisson_client.start_num_for_version}")
    private int startNumForKey;

    public PostDto create(PostDto postDto) {
        validator.validateBeforeCreate(postDto);
        Post postEntity = mapper.toEntity(postDto);
        postEntity = preparer.prepareForCreate(postDto, postEntity);
        Post createdEntity = postRepository.save(postEntity);
        userCacheService.saveCacheUser(createdEntity.getAuthorId());
        createCachePostAndSave(createdEntity);
        sendKafkaEvent(createdEntity);
        return mapper.toDto(createdEntity);
    }

    public PostDto publish(Long postId) {
        Post entity = getPostEntity(postId);
        validator.validatePublished(entity);
        Post publishedPost = preparer.prepareForPublish(entity);

        publishedPost = postRepository.save(publishedPost);
        log.info("Published post: {}", publishedPost);

        return mapper.toDto(publishedPost);
    }

    public PostDto update(PostDto postDto) {
        Post entity = getPostEntity(postDto.getId());
        validator.validateBeforeUpdate(postDto, entity);

        Post updatedEntity = preparer.prepareForUpdate(postDto, entity);
        updatedEntity = postRepository.save(updatedEntity);
        log.info("Updated post: {}", updatedEntity);

        return mapper.toDto(updatedEntity);
    }

    public PostDto delete(Long postId) {
        Post entity = getPostEntity(postId);
        validator.validateDeleted(entity);

        entity.setPublished(false);
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        Post deletedEntity = postRepository.save(entity);
        log.info("Deleted post: {}", deletedEntity);

        return mapper.toDto(deletedEntity);
    }

    public PostDto getPost(Long postId) {
        PostDto dto = mapper.toDto(getPostEntity(postId));
        kafkaPostViewProducer.publish(mapper.toKafkaPostViewDto(dto));
        return dto;
    }

    public List<PostDto> getFilteredPosts(PostFilterDto filters) {
        List<PostFilter> actualPostFilters = postFilters.stream()
                .filter(f -> f.isApplicable(filters)).toList();
        List<PostDto> postDtoList = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> actualPostFilters.stream()
                        .allMatch(filter -> filter.test(post, filters)))
                .map(mapper::toDto)
                .toList();
        postDtoList.forEach(dto -> kafkaPostViewProducer.publish(mapper.toKafkaPostViewDto(dto)));
        return postDtoList;
    }

    public List<PostDto> getPostsByAuthorIds(List<Long> menteesIds, Long startPostId, int batchSize) {
        List<Post> posts = postRepository
                .findPostsByAuthorIds(menteesIds, startPostId, PageRequest.of(0, batchSize));
        return posts.stream()
                .map(mapper::toDto)
                .toList();
    }

    private void createCachePostAndSave(Post createdEntity) {
        CachePost cachePost = mapper.toCachePost(createdEntity);
        RMap<String, Integer> versionMap = redissonClient.getMap(version);
        versionMap.put(versionedKey, startNumForKey);
        cachePost.setVersion(versionMap);
        cachePostRepository.save(cachePost);
    }

    private void sendKafkaEvent(Post createdEntity) {
        KafkaPostDto kafkaDto = mapper.toKafkaPostDto(createdEntity);
        kafkaDto.setSubscriberIds(userClient.getUser(createdEntity.getAuthorId()).getFollowersIds());
        kafkaPostProducer.publish(kafkaDto);
    }

    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("There is no such message"));
    }
}
