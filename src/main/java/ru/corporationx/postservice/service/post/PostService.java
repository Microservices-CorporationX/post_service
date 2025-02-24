package ru.corporationx.postservice.service.post;

import ru.corporationx.postservice.config.context.UserContext;
import ru.corporationx.postservice.dto.post.PostAuthorFilterDto;
import ru.corporationx.postservice.dto.post.PostDto;
import ru.corporationx.postservice.dto.resource.ResourceDto;
import ru.corporationx.postservice.dto.resource.ResourceInfoDto;
import ru.corporationx.postservice.dto.user.BanUsersDto;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.mapper.PostViewEventMapper;
import ru.corporationx.postservice.mapper.post.PostMapper;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.publisher.postview.PostViewEventPublisher;
import ru.corporationx.postservice.publisher.user.UserBanPublisher;
import ru.corporationx.postservice.repository.PostRepository;
import ru.corporationx.postservice.service.image.ImageResizeService;
import ru.corporationx.postservice.service.resource.ResourceService;
import ru.corporationx.postservice.validator.post.ContentValidator;
import ru.corporationx.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${app.posts.files.picture-max-width:10}")
    private int maxImageWidth;
    @Value("${app.posts.files.picture-max-height:10}")
    private int maxImageHeight;
    @Value("${banner.minimum-size-of-unverified-posts:10}")
    private int minimumSizeOfUnverifiedPosts;

    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostViewEventPublisher postViewEventPublisher;
    private final UserContext userContext;
    private final PostViewEventMapper postViewEventMapper;
    private final ResourceService resourceService;
    private final ImageResizeService imageResizeService;
    private final UserBanPublisher userBanPublisher;
    private final ContentValidator contentValidator;

    public Post findEntityById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Incorrect post id"));
    }

    public PostDto create(PostDto postDto) {
        postValidator.validateCreation(postDto);
        if (!Boolean.TRUE.equals(postDto.getPublished())) {
            postDto.setPublished(false);
        } else {
            postDto.setPublishedAt(LocalDateTime.now());
        }

        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setUpdatedAt(LocalDateTime.now());

        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publish(long postId) {
        Post post = findEntityById(postId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto update(PostDto postDto) {
        Post post = findEntityById(postDto.getId());
        postValidator.validateUpdate(post, postDto);

        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto deletePost(long id) {
        Post post = findEntityById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Post already deleted");
        }
        post.setPublished(false);
        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostDto> getPostsBy(PostAuthorFilterDto filter) {
        postValidator.validateFilter(filter);

        if (filter.isPublished() && filter.getAuthorId() != null) {
            return getAllPublishedByAuthorId(filter.getAuthorId());
        } else if (filter.isPublished() && filter.getProjectId() != null) {
            return getAllPublishedByProjectId(filter.getProjectId());
        } else if (!filter.isPublished() && filter.getAuthorId() != null) {
            return getAllNonPublishedByAuthorId(filter.getAuthorId());
        } else if (!filter.isPublished() && filter.getProjectId() != null) {
            return getAllNonPublishedByProjectId(filter.getProjectId());
        }
        return List.of();
    }

    public List<PostDto> getAllNonPublishedByAuthorId(long id) {
        postValidator.validateUser(id);
        return filterNonPublishedPostsByTimeToDto(postRepository.findByAuthorIdWithLikes(id));
    }

    public List<PostDto> getAllNonPublishedByProjectId(long id) {
        postValidator.validateProject(id);
        return filterNonPublishedPostsByTimeToDto(postRepository.findByProjectIdWithLikes(id));
    }

    public List<PostDto> getAllPublishedByAuthorId(long id) {
        postValidator.validateUser(id);
        List<PostDto> postDtos = filterPublishedPostsByTimeToDto(postRepository.findByAuthorIdWithLikes(id));
        publishPostViewEvent(postDtos);
        return postDtos;
    }

    public List<PostDto> getAllPublishedByProjectId(long id) {
        postValidator.validateProject(id);
        List<PostDto> postDtos = filterPublishedPostsByTimeToDto(postRepository.findByProjectIdWithLikes(id));
        publishPostViewEvent(postDtos);
        return postDtos;
    }


    private List<PostDto> filterPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> filterNonPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional
    public List<ResourceDto> addPictures(long postId, MultipartFile[] files) {
        Post post = findEntityById(postId);
        postValidator.validateMedia(post, files);
        List<ResourceInfoDto> resourcesInfo = Arrays.stream(files)
                .map(f -> preprocessFiles(postId, f))
                .toList();
        return resourceService.uploadResources(post, resourcesInfo);
    }

    private ResourceInfoDto preprocessFiles(long postId, MultipartFile file) {
        byte[] bytes = imageResizeService.resizeAndConvert(file, maxImageWidth, maxImageHeight);
        return ResourceInfoDto.builder()
                .key(String.format("%s_%s_%s", postId, file.getOriginalFilename(), LocalDateTime.now()))
                .name(file.getOriginalFilename())
                .type("image/jpeg")
                .bytes(bytes)
                .build();
    }

    private void publishPostViewEvent(List<PostDto> postDtos) {
        long actorId = userContext.getUserId();
        postValidator.validateUser(actorId);
        postDtos.stream()
                .map(postDto -> postViewEventMapper.toAnalyticsEventDto(postDto, actorId))
                .forEach(postViewEventPublisher::publish);
    }

    public void banUsers() {
        List<Post> postsWithOffensiveContent = postRepository.findNotVerifiedPots()
                .orElseGet(() -> null);
        if (postsWithOffensiveContent == null) {
            log.info("users for ban not found! cause not posts which not verified");
            return;
        }
        List<Long> banningUsersIds = getBanningUsersIds(postsWithOffensiveContent);
        if (banningUsersIds != null && banningUsersIds.isEmpty()) {
            log.info("users for ban not found!, " +
                    "cause no users who have unverified posts exceeding {}", minimumSizeOfUnverifiedPosts);
            return;
        }
        log.info("users for ban received! users ids: {}", banningUsersIds);
        userBanPublisher.publish(BanUsersDto
                .builder()
                .usersIds(banningUsersIds)
                .build());
    }

    public List<Long> getBanningUsersIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getAuthorId)
                .filter(authorId -> Collections.frequency(posts
                        .stream().map(Post::getAuthorId).toList(), authorId) >= minimumSizeOfUnverifiedPosts
                )
                .distinct()
                .toList();
    }

    public void checkText() {
        List<Post> nonPublishedPosts = postRepository.findReadyToPublish();
        nonPublishedPosts.forEach(contentValidator::processPost);
        postRepository.saveAll(nonPublishedPosts);
    }
}