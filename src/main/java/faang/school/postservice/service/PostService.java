package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.UserFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.KafkaPostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${post.publish-post.batch-size}")
    private int batchSize;

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;
    private final OrthographyService orthographyService;
    private final PostViewEventPublisher postViewEventPublisher;
    private final KafkaPostProducer kafkaPostProducer;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public Long createDraftPost(PostDto postDto) {
        checkAuthorIdExist(postDto.userId(), postDto.projectId());
        Post post = postRepository.save(postMapper.toEntity(postDto));
        return post.getId();
    }

    @CachePut(value = "postDto", key = "#postId")
    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        checkPostIsNotPublishedAndNotDeleted(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        List<Long> followerList = userServiceClient.getFollowingUsers(post.getAuthorId(), new UserFilterDto()).stream().map(UserDto::id).toList();
        // System.out.println(followerList);
        kafkaPostProducer.sendPostCreatedEvent(postId, post.getAuthorId(), followerList);
        return postMapper.toDto(post);
    }

    @CachePut(value = "postDto", key = "#postId")
    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        post.setContent(postDto.content());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public Long deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        checkPostWasNotDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
        return post.getId();
    }

    @Cacheable(value = "postDto", key = "#postId")
    public PostDto getPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            PostDto postDto = postMapper.toDto(post);
            postViewEventPublisher.publish(new PostViewEvent(postId, post.getAuthorId(),
                    userContext.getUserId(), LocalDateTime.now()));
            return postDto;
        } else throw new EntityNotFoundException("Post not found with ID: " + postId);
    }

    public List<PostDto> getDraftPostsForUser(Long idUser) {
        checkUserExistById(idUser);
        return postRepository.findByAuthorId(idUser)
                .stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getDraftPostsForProject(Long idProject) {
        return postRepository.findByProjectId(idProject)
                .stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @CachePut(value = "postList", key = "#idUser")
    public List<PostDto> getPublishedPostsForUser(Long idUser) {
        checkUserExistById(idUser);
        List<Post> postList = postRepository.findByAuthorId(idUser)
                .stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
        List<PostDto> postDtoList = postList.stream().map(postMapper::toDto)
                .toList();
        postList.forEach(post -> postViewEventPublisher.publish(new PostViewEvent(post.getId(),
                post.getAuthorId(), userContext.getUserId(), LocalDateTime.now())));
        return postDtoList;
    }

    @CachePut(value = "postList", key = "#idProject")
    public List<PostDto> getPublishedPostForProject(Long idProject) {
        checkProjectExistById(idProject);
        List<Post> postList = postRepository.findByProjectId(idProject)
                .stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed()).toList();
        List<PostDto> postDtoList = postList.stream().map(postMapper::toDto).toList();
        postList.forEach(post -> postViewEventPublisher.publish(new PostViewEvent(post.getId(),
                post.getAuthorId(), userContext.getUserId(), LocalDateTime.now())));
        return postDtoList;
    }


    public void checkGrammarPostContentAndChangeIfNeed() {
        List<Post> posts = getAllUnpublishedPostsOrThrow();
        posts.forEach(post -> {
            post.setContent(orthographyService.getCorrectContent(post.getContent(), post.getId()));
            postRepository.save(post);
        });
    }

    public void publishScheduledPosts() {
        List<Post> allToPublishPosts = postRepository.findReadyToPublish();

        if (allToPublishPosts.size() == 0) {
            return;
        }

        int quantity = allToPublishPosts.size() == batchSize ? 0 : allToPublishPosts.size() / batchSize;
        LocalDateTime now = LocalDateTime.now();

        log.info("Posts publishing: batch quantity = {}", quantity + 1);

        for (int i = 0; i <= quantity; i++) {
            int fromIndex = batchSize * i;
            int toIndex = Math.min(allToPublishPosts.size(), batchSize * (i + 1));

            List<Post> readyToPublishPosts = allToPublishPosts.subList(fromIndex, toIndex);

            int batchNumber = i + 1;
            CompletableFuture
                    .supplyAsync(() -> this.publishBatch(readyToPublishPosts, now), threadPoolTaskExecutor)
                    .exceptionally((ex) -> {
                        log.error("Posts publishing: batch {} has not been published, size = {}", batchNumber, readyToPublishPosts.size());
                        return new ArrayList<>();
                    })
                    .thenAccept(list -> log.info("Posts publishing: batch {} has been published, size = {}", batchNumber, readyToPublishPosts.size()));
        }
    }

    @Transactional
    public List<Post> publishBatch(List<Post> readyToPublishPosts, LocalDateTime now) {
        for (Post post : readyToPublishPosts) {
            post.setPublished(true);
            post.setPublishedAt(now);
        }

        List<Post> savedPosts = StreamSupport
                .stream(postRepository.saveAll(readyToPublishPosts).spliterator(), false)
                .collect(Collectors.toList());

        return savedPosts;
    }

    private List<Post> getAllUnpublishedPostsOrThrow() {
        List<Post> unpublishedPosts = StreamSupport
                .stream(postRepository.findAll().spliterator(), false)
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .collect(Collectors.toList());
        if (unpublishedPosts.isEmpty()) {
            log.error("The list of unpublished posts is null.");
            throw new EntityNotFoundException("The list of unpublished posts is null.");
        }
        return unpublishedPosts;
    }

    private void checkIdUserAndIdProjectNotEquals(Long idUser, Long idProject) {
        if (Objects.equals(idUser, idProject)) {
            log.error(String.format("idProject %s and idUser %s equals", idProject, idUser));
            throw new IllegalArgumentException(String.format("idProject %s and idUser %s equals", idProject, idUser));
        }
    }

    private void checkPostIsNotPublishedAndNotDeleted(Post post) {
        if (post.isPublished()) {
            log.error("Post already published, id:" + post.getId());
            throw new IllegalArgumentException("Post already published, id: " + post.getId());
        }
        if (post.isDeleted()) {
            log.error("Post was deleted, id:" + post.getId());
            throw new IllegalArgumentException("Post was deleted, id: " + post.getId());
        }
    }

    private void checkAuthorIdExist(Long idUser, Long idProject) {
        checkIdUserAndIdProjectNotEquals(idUser, idProject);
        String temp = "User id: ";
        try {
            if (idUser != null) {
                userServiceClient.getUser(idUser);
            } else if (idProject != null) {
                temp = "Project id: ";
                projectServiceClient.getProject(idProject);
            }
        } catch (FeignException e) {
            switch (e.status()) {
                case 404:
                    log.error(temp + idUser + " not found " + HttpStatus.NOT_FOUND);
                    throw new IllegalArgumentException(temp + idUser + " not found" + HttpStatus.NOT_FOUND);
                case 500:
                    log.error(temp + idUser + " Internal Server Error " + HttpStatus.INTERNAL_SERVER_ERROR);
                    throw new IllegalArgumentException(temp + idUser + " Internal Server Error" + HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    log.error(temp + idUser + " Error " + e.getMessage());
                    throw new IllegalArgumentException(temp + idUser + " Error " + e.getMessage());
            }
        }
    }

    private void checkUserExistById(Long idUser) {
        try {
            userServiceClient.getUser(idUser);
        } catch (FeignException e) {
            log.error("User id:" + idUser + "Error" + e.getMessage());
            throw new IllegalArgumentException("User id:" + idUser + "Error" + e.getMessage());
        }
    }

    private void checkProjectExistById(Long idProject) {
        try {
            projectServiceClient.getProject(idProject);
        } catch (FeignException e) {
            log.error("Project id:" + idProject + "Error" + e.getMessage());
            throw new IllegalArgumentException("Project id:" + idProject + "Error" + e.getMessage());
        }
    }

    private void checkPostWasNotDeleted(Post post) {
        if (post.isDeleted()) {
            log.error("Post with id: " + post.getId() + " was deleted");
            throw new IllegalArgumentException("Post with id: " + post.getId() + " was deleted");
        }
    }
}