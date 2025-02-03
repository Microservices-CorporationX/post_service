package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.thread_pool.PostPublishExecutorConfig;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.kafka.event.PostPublishedEvent;
import faang.school.postservice.kafka.producer.PostPublishedEventProducer;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.RedisMessagePublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import faang.school.postservice.service.redis.PostCacheService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final RedisMessagePublisher redisMessagePublisher;
    private final ObjectMapper objectMapper;
    private final PostPublishExecutorConfig postPublishExecutorConfig;
    private final HashtagService hashtagService;
    private final PostCacheService postCacheService;
    private final AsyncPostService asyncPostService;
    private final PostPublishedEventProducer postPublishedEventProducer;
    private final UserServiceClient userServiceClient;

    @Value("${post.unverified-posts-ban-count}")
    private Integer unverifiedPostsBanCount;

    @Value("${post.publish-posts.batch-size}")
    private int postPublishBatchSize;

    @Value("${post.author-followers.batch-size}")
    private int authorFollowersBatchSize;

    public PostDto createPost(PostRequestDto postRequestDtoDto) {
        postValidator.checkCreator(postRequestDtoDto);

        Post createPost = postMapper.toEntity(postRequestDtoDto);
        createPost.setPublished(false);
        createPost.setDeleted(false);

        createPost = postRepository.save(createPost);
        log.info("Post with id {} - created", createPost.getId());
        hashtagService.takeHashtags(createPost);
        return postMapper.toDto(createPost);
    }

    public PostDto publishPost(Long postId) {
        Post post = getPost(postId);
        if (post.isPublished()) {
            throw new PostException("Forbidden republish post");
        }
        post.setPublished();
        postCacheService.save(postMapper.toCachePost(post));
        postPublishedEventProducer.send(new PostPublishedEvent(post.getAuthorId(), List.of(postMapper.toEventPostDto(post))));
        log.info("Post with ID={} was published", postId);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPost(postDto.getId());
        postValidator.checkUpdatePost(post, postDto);

        postMapper.updatePostFromDto(postDto, post);

        hashtagService.checkHashtags(post);
        Post updatedPost = postRepository.save(post);
        postCacheService.update(postMapper.toCachePost(post));
        log.info("Post with id {} - updated", post.getId());
        return postMapper.toDto(updatedPost);
    }

    public void disablePostById(Long postId) {
        Post deletePost = getPost(postId);
        deletePost.setDeleted(true);
        postCacheService.delete(postId);

        log.info("Post with id {} - deleted", deletePost.getId());
        postRepository.save(deletePost);
    }

    public PostDto getPostById(Long postId) {
        Post post = getPost(postId);

        return postMapper.toDto(post);
    }

    public List<PostDto> getAllNoPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();

        log.info("Get all drafts posts with author id {}", userId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllNoPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();

        log.info("Get all drafts posts with project id {}", projectId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();

        log.info("Get all posts with author id {}", userId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();

        log.info("Get all posts with project id {}", projectId);
        return postMapper.toDto(posts);
    }

    public void addLikeToPost(long postId, Like like) {
        Post post = getPost(postId);
        post.getLikes().add(like);

        log.info("Adding like to post with id {}", postId);
        postRepository.save(post);
    }

    public void removeLikeFromPost(long postId, Like like) {
        Post post = getPost(postId);
        post.getLikes().remove(like);

        log.info("Removing like from post with id {}", postId);
        postRepository.save(post);
    }

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    public void getPostsWhereVerifiedFalse() {
        try {
            List<Long> authorIds = postRepository.findAuthorsIdsToBan(unverifiedPostsBanCount);

            if (authorIds.isEmpty()) {
                log.info("No authors to ban");
                return;
            }

            for (Long authorId : authorIds) {
                String message = objectMapper.writeValueAsString(authorId);
                log.info("Message sent to Redis with authorId : {}", authorId);
                redisMessagePublisher.publish(message);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize author ID to JSON", e);
        }
    }

    @Transactional(readOnly = true)
    @Async("threadPoolExecutorForPublishingPosts")
    public void publishScheduledPosts() {
        List<Post> postsToPublish = postRepository.findReadyToPublish();
        log.info("Publishing {} scheduled posts...", postsToPublish.size());
        List<CompletableFuture<Void>> publishingPostsFeatures = getPublishingPostsFeatures(postsToPublish);
        CompletableFuture.allOf(publishingPostsFeatures.toArray(new CompletableFuture[0])).join();
        log.info("Finished publishing {} scheduled posts", postsToPublish.size());
    }

    private List<CompletableFuture<Void>> getPublishingPostsFeatures(List<Post> posts) {
        return ListUtils.partition(posts, postPublishBatchSize).stream()
                .map(asyncPostService::publishPosts)
                .toList();
    }
}