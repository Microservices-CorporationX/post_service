package faang.school.postservice.service.post;

import faang.school.postservice.annotations.SendPostCreatedEvent;
import faang.school.postservice.annotations.SendPostViewEventToAnalytics;
import faang.school.postservice.annotations.SendPostViewEventToKafka;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.dto.redis.CommentRedisEntity;
import faang.school.postservice.dto.redis.PostRedisEntity;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.post.image.DownloadImageFromPostException;
import faang.school.postservice.exception.post.image.UploadImageToPostException;
import faang.school.postservice.exception.spelling_corrector.DontRepeatableServiceException;
import faang.school.postservice.exception.spelling_corrector.RepeatableServiceException;
import faang.school.postservice.kafka.KafkaPostProducer;
import faang.school.postservice.kafka.dto.CommentCreatedKafkaDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.service.aws.s3.S3Service;
import faang.school.postservice.service.post.cache.PostCacheProcessExecutor;
import faang.school.postservice.service.post.cache.PostCacheService;
import faang.school.postservice.service.post.hash.tag.PostHashTagParser;
import faang.school.postservice.service.user.UserCacheService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static faang.school.postservice.model.VerificationPostStatus.REJECTED;
import static faang.school.postservice.model.VerificationPostStatus.UNVERIFIED;
import static faang.school.postservice.utils.ImageRestrictionRule.POST_IMAGES;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${post.images.bucket.name-prefix}")
    private String bucketNamePrefix;

    @Value("${app.post.cache.number_of_top_in_cache}")
    private int numberOfTopInCache;

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final SpellingCorrectionService spellingCorrectionService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostHashTagParser postHashTagParser;
    private final PostCacheProcessExecutor postCacheProcessExecutor;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;
    private final PostRedisRepository postRedisRepository;
    private final KafkaPostProducer kafkaPostProducer;
    private final RedisTemplate<String, Object> commonRedisTemplate;
    private final UserCacheService userCacheService;
    private final CommentRedisRepository commentRedisRepository;

    @Transactional
    @SendPostCreatedEvent
    public Post create(Post post) {
        log.info("Create post with id: {}", post.getId());
        postValidator.validateCreatePost(post);

        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setVerificationStatus(UNVERIFIED);
        postHashTagParser.updateHashTags(post);

        return postRepository.save(post);
    }

    @Transactional
    public Post publish(Long id) {
        log.info("Publish post with id: {}", id);
        Post post = findPostById(id);

        if (post.isPublished()) {
            throw new PostPublishedException(id);
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postHashTagParser.updateHashTags(post);
        postRepository.save(post);
        userCacheService.saveUserToRedisRepository(post.getAuthorId());

        postCacheProcessExecutor.executeNewPostProcess(postMapper.toPostCacheDto(post));

        return post;
    }

    @Transactional
    public Post update(Post updatePost) {
        log.info("Update post with id: {}", updatePost.getId());
        Post post = findPostById(updatePost.getId());
        List<String> primalTags = new ArrayList<>(post.getHashTags());

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postHashTagParser.updateHashTags(post);

        postRepository.save(post);

        if (!post.isDeleted() && post.isPublished()) {
            postCacheProcessExecutor.executeUpdatePostProcess(postMapper.toPostCacheDto(post), primalTags);
        }
        return post;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Delete post with id: {}", id);
        Post post = findPostById(id);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postCacheProcessExecutor.executeDeletePostProcess(postMapper.toPostCacheDto(post), post.getHashTags());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostCacheDto> findInRangeByHashTag(String hashTag, int start, int end) {
        List<PostCacheDto> postDtos = postCacheService.findInRangeByHashTag(hashTag, start, end);

        if (postDtos.isEmpty() && postCacheService.isRedisConnected()) {
            String jsonTag = postHashTagParser.convertTagToJson(hashTag);
            List<Post> posts = postRepository.findTopByHashTagByDate(jsonTag, numberOfTopInCache);
            List<PostCacheDto> postDtosByTop = postMapper.mapToPostCacheDtos(posts);
            postDtos = postDtosByTop.subList(start, Math.min(postDtosByTop.size(), end));

            postCacheProcessExecutor.executeAddListOfPostsToCache(postDtosByTop, hashTag);
        } else if (postDtos.isEmpty()) {
            String jsonTag = postHashTagParser.convertTagToJson(hashTag);
            List<Post> posts = postRepository.findInRangeByHashTagByDate(jsonTag, start, end);
            postDtos = postMapper.mapToPostCacheDtos(posts);
        }
        return postDtos;
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        return postRepository.findByIdAndNotDeleted(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @SendPostViewEventToAnalytics(Post.class)
    @SendPostViewEventToKafka(Post.class)
    public Post get(Long postId) {
        return findPostById(postId);
    }

    @Transactional(readOnly = true)
    @SendPostViewEventToAnalytics(List.class)
    @SendPostViewEventToKafka(value = List.class, elementType = Post.class)
    public List<Post> searchByAuthor(Post filterPost) {
        List<Post> posts = postRepository.findByAuthorId(filterPost.getAuthorId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    @Transactional(readOnly = true)
    @SendPostViewEventToAnalytics(List.class)
    @SendPostViewEventToKafka(value = List.class, elementType = Post.class)
    public List<Post> searchByProject(Post filterPost) {
        List<Post> posts = postRepository.findByProjectId(filterPost.getProjectId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    @Transactional(readOnly = true)
    public List<Long> findUserIdsForBan() {
        return postRepository.findAllUsersBorBan(REJECTED);
    }

    public void correctPosts(List<Post> draftPosts) {
        draftPosts.forEach(post -> {
            try {
                String correctedContent = spellingCorrectionService.getCorrectedContent(post.getContent());

                LocalDateTime currentTime = LocalDateTime.now();
                post.setContent(correctedContent);
                post.setUpdatedAt(currentTime);
                post.setScheduledAt(currentTime);
            } catch (RepeatableServiceException exception) {
                log.error("Контент поста {} не прошёл авто корректировку, после переотправок", post.getId());
            } catch (DontRepeatableServiceException exception) {
                log.error("Контент поста {} не прошёл авто корректировку из-за ошибки сервиса", post.getId());
            }
        });

        postRepository.saveAll(draftPosts);
    }

    private Stream<Post> applyFiltersAndSorted(List<Post> posts, Post filterPost) {
        return posts.stream()
                .filter((post -> !post.isDeleted()))
                .filter((post -> post.isPublished() == filterPost.isPublished()))
                .sorted(Comparator.comparing(
                        filterPost.isPublished() ? Post::getPublishedAt : Post::getCreatedAt
                ).reversed());
    }

    @Transactional
    public void uploadImages(Long postId, List<MultipartFile> images) {
        log.info("Upload images to post {}", postId);
        postValidator.validateImagesToUpload(postId, images);

        List<Resource> resourcesToSave = new ArrayList<>();

        Post post = findPostById(postId);
        String folder = bucketNamePrefix + post.getId();
        images.forEach(image -> {
            try {
                Resource uploadedImage = s3Service.uploadFile(image, folder, POST_IMAGES);
                uploadedImage.setPost(post);
                resourcesToSave.add(uploadedImage);
                log.info("Image {} was uploaded to S3", uploadedImage.getId());
            } catch (Exception e) {
                log.error("Failed to upload image {} to S3", image.getOriginalFilename(), e);
                throw new UploadImageToPostException(image.getOriginalFilename(), postId);
            }
        });

        resourceRepository.saveAll(resourcesToSave);
        log.info("Images successfully uploaded to post {}", postId);
    }

    @Transactional(readOnly = true)
    public Resource findResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }

    public org.springframework.core.io.Resource downloadImage(Resource resource) {
        log.info("Download image {}", resource.getId());
        try (InputStream inputStream = s3Service.downloadFile(resource.getKey())) {
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("Failed to download image {}", resource.getId(), e);
            throw new DownloadImageFromPostException(resource.getId());
        }
    }

    @Transactional
    public void deleteImagesFromPost(List<Long> resourceIds) {
        log.info("Delete images");
        List<Resource> resources = resourceRepository.findAllByIdIn(resourceIds);

        List<String> keys = resources.stream().map(Resource::getKey).toList();
        s3Service.deleteFiles(keys);

        resourceRepository.deleteAll(resources);
        log.info("Images successfully deleted");
    }

    @Transactional(readOnly = true)
    public List<Long> getReadyToPublishIds() {
        return postRepository.findReadyToPublishIds();
    }

    @Async("postExecutorPool")
    @Transactional
    public void processReadyToPublishPosts(List<Long> postIds) {
        List<Post> postList = postRepository.findPostsByIds(postIds);
        postList.forEach(post -> {
            post.setPublishedAt(LocalDateTime.now());
            post.setPublished(true);
        });
        List<Long> authorIds = postList.stream().map(Post::getAuthorId).toList();
        userCacheService.saveAllToRedisRepository(authorIds);
        postRepository.saveAll(postList);

        List<PostRedisEntity> postDtos = postMapper.mapToPostRedisDtos(postList);
        postRedisRepository.saveAll(postDtos);

        kafkaPostProducer.sendPostsToKafka(postList);
        log.info("Posts was published by scheduling: {}", postIds);
    }

    public List<PostRedisEntity> getRedisPostsById(Set<Long> postIds) {
        Iterable<PostRedisEntity> iterablePosts = postRedisRepository.findAllById(postIds);
        List<PostRedisEntity> posts = StreamSupport.stream(iterablePosts.spliterator(), false)
                .toList();
        if (postIds.size() == posts.size()) {
            return posts.stream()
                    .sorted(Comparator.comparing(PostRedisEntity::getPublishedAt))
                    .toList();
        } else {
            Set<Long> existingPostIds = posts.stream()
                    .map(PostRedisEntity::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingIds = postIds.stream()
                    .filter(id -> !existingPostIds.contains(id))
                    .collect(Collectors.toSet());
            List<Post> postsFromDb = postRepository.findPostsByIds(missingIds);
            posts = new ArrayList<>(posts);
            posts.addAll(postMapper.mapToPostRedisDtos(postsFromDb));
            return posts.stream()
                    .sorted(Comparator.comparing(PostRedisEntity::getPublishedAt))
                    .toList();
        }
    }

    public void incrementView(Long postId) {
        commonRedisTemplate.opsForHash().increment("PostRedisEntity:" + postId, "views", 1L);
    }

    public void changeLike(Long postId, LikeAction likeAction) {
        long delta = likeAction == LikeAction.ADD ? 1L : -1L;
        commonRedisTemplate.opsForHash().increment("PostRedisEntity:" + postId, "likes", delta);
    }

    public void changeCommentLike(Long postId, Long commentId, LikeAction likeAction) {
        int delta = likeAction == LikeAction.ADD ? 1 : -1;
        commonRedisTemplate.opsForHash().increment("CommentRedisEntity:" + commentId, "likes", delta);
    }

    public void addCommentToPost(CommentCreatedKafkaDto commentCreatedKafkaDto) {
        Long postId = commentCreatedKafkaDto.getPostId();
        Long commentId = commentCreatedKafkaDto.getCommentId();
        String content = commentCreatedKafkaDto.getContent();
        long authorId = commentCreatedKafkaDto.getAuthorId();

        CommentRedisEntity commentRedisEntity = CommentRedisEntity.builder()
                .id(commentId)
                .content(content)
                .likes(0)
                .authorId(authorId)
                .build();

        commentRedisRepository.save(commentRedisEntity);

        PostRedisEntity postRedisEntity = postRedisRepository.findById(postId).orElse(new PostRedisEntity());
        List<Long> comments = postRedisEntity.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
        }
        if (comments.size() >= 3) {
            comments.remove(0);
        }
        comments.add(commentId);

        postRedisEntity.setComments(comments);
        postRedisRepository.save(postRedisEntity);
    }
}
