package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.ExternalServiceValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    public static final String EXTERNAL_ERROR_MESSAGE = "Error occurred from external service: ";
    public static final String POST_WITH_ID_NOT_FOUND = "Post with id %s not found";
    public static final String POST_WITH_ID_ALREADY_PUBLISHED = "Post with ID %s is already published";
    public static final String POSTS_BY_USER_ID_NOT_FOUND = "Posts by user ID: %s not found";
    public static final String POSTS_BY_PROJECT_ID_NOT_FOUND = "Posts by project ID: %s not found";
    public static final String POSTS_MUST_HAVE_ONE_AUTHOR = "Post must have exactly one author (either user or project).";
    public static final String PROJECT_WITH_ID_NOT_FOUND = "Project with ID %d not found";
    public static final String DRAFTS_BY_USER_ID_NOT_FOUND = "Drafts by user ID: %s not found";
    public static final String DRAFTS_BY_PROJECT_ID_NOT_FOUND = "Drafts by project ID: %s not found";
    public static final String USER_WITH_ID_NOT_FOUND = "User with ID %d not found";

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    @Override
    public PostDto createDraft(PostDto postDto) {
        validateDraft(postDto);
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setPublished(false);
        Post post = postMapper.toEntity(postDto);
        if (postDto.getAuthorId() != null) {
            getUserWithValidation(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            getProjectWithValidation(postDto.getProjectId());
        }
        return savePostAndMapToDto(post);
    }

    @Override
    public PostDto publish(Long postId) {
        return postRepository.findById(postId)
                .map(post -> {
                    if (post.isPublished()) {
                        throw new ExternalServiceValidationException(String.format(POST_WITH_ID_ALREADY_PUBLISHED, postId));
                    }
                    post.setPublishedAt(LocalDateTime.now());
                    post.setPublished(true);

                    return savePostAndMapToDto(post);
                })
                .orElseThrow(() -> new PostNotFoundException(String.format(POST_WITH_ID_NOT_FOUND, postId)));
    }

    @Override
    public PostDto update(Long id, String content) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setContent(content);
                    post.setUpdatedAt(LocalDateTime.now());

                    return savePostAndMapToDto(post);
                })
                .orElseThrow(() -> new PostNotFoundException(String.format(POST_WITH_ID_NOT_FOUND, id)));
    }

    @Override
    public PostDto softDelete(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setDeleted(true);
                    return savePostAndMapToDto(post);
                })
                .orElseThrow(() -> new PostNotFoundException(String.format(POST_WITH_ID_NOT_FOUND, id)));
    }

    @Override
    public PostDto getById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(() -> new PostNotFoundException(String.format(POST_WITH_ID_NOT_FOUND, id)));
    }

    @Override
    public List<PostDto> getNotDeletedDraftsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(String.format(DRAFTS_BY_USER_ID_NOT_FOUND, userId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(String.format(DRAFTS_BY_PROJECT_ID_NOT_FOUND, projectId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedPublishedPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(String.format(POSTS_BY_USER_ID_NOT_FOUND, userId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(String.format(POSTS_BY_PROJECT_ID_NOT_FOUND, projectId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    private void validateDraft(PostDto postDto) {
        if ((postDto.getAuthorId() == null && postDto.getProjectId() == null) ||
                (postDto.getAuthorId() != null && postDto.getProjectId() != null)) {
            throw new ExternalServiceValidationException(POSTS_MUST_HAVE_ONE_AUTHOR);
        }
    }

    private void getUserWithValidation(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException exception) {
            handleFeignException(exception, String.format(USER_WITH_ID_NOT_FOUND, userId));
        }
    }

    private void getProjectWithValidation(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException exception) {
            handleFeignException(exception, String.format(PROJECT_WITH_ID_NOT_FOUND, projectId));
        }
    }

    private void handleFeignException(FeignException exception, String notFoundMessage) {
        if (exception.status() == HttpStatus.NOT_FOUND.value()) {
            throw new PostNotFoundException(notFoundMessage);
        }
        throw new ExternalServiceValidationException(EXTERNAL_ERROR_MESSAGE + exception.getMessage());
    }

    private PostDto savePostAndMapToDto(Post post) {
        return postMapper.toDto(postRepository.save(post));
    }
}
