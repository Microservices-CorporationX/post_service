package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostValidationException;
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
import java.util.Optional;

import static faang.school.postservice.constant.PostErrorMessages.*;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
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
            validateUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            validateProject(postDto.getProjectId());
        }
        return savePostAndMapToDto(post);
    }

    @Override
    public PostDto publish(Long postId) {
        return postRepository.findById(postId)
                .map(post -> {
                    if (post.isPublished()) {
                        throw new PostValidationException(String.format(POST_WITH_ID_ALREADY_PUBLISHED, postId));
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
        return Optional.of(postRepository.findByAuthorId(userId))
                .filter(posts -> !posts.isEmpty())
                .map(posts -> posts.stream()
                        .filter(post -> !post.isDeleted())
                        .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                        .map(postMapper::toDto)
                        .toList())
                .orElseThrow(() -> new PostNotFoundException(String.format(
                        DRAFTS_BY_USER_ID_NOT_FOUND, userId)));
    }

    @Override
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        return Optional.of(postRepository.findByProjectId(projectId))
                .filter(posts -> !posts.isEmpty())
                .map(posts -> posts.stream()
                        .filter(post -> !post.isDeleted())
                        .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                        .map(postMapper::toDto)
                        .toList())
                .orElseThrow(() -> new PostNotFoundException(String.format(
                        DRAFTS_BY_PROJECT_ID_NOT_FOUND, projectId)));
    }

    @Override
    public List<PostDto> getNotDeletedPublishedPostsByUserId(Long userId) {
        return Optional.of(postRepository.findByAuthorId(userId))
                .filter(posts -> !posts.isEmpty())
                .map(posts -> posts.stream()
                        .filter(post -> !post.isDeleted() && post.isPublished())
                        .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                        .map(postMapper::toDto)
                        .toList())
                .orElseThrow(() -> new PostNotFoundException(String.format(
                        POSTS_BY_USER_ID_NOT_FOUND, userId)));
    }

    @Override
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        return Optional.of(postRepository.findByProjectId(projectId))
                .filter(posts -> !posts.isEmpty())
                .map(posts -> posts.stream()
                        .filter(post -> !post.isDeleted() && post.isPublished())
                        .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                        .map(postMapper::toDto)
                        .toList())
                .orElseThrow(() -> new PostNotFoundException(String.format(
                        POSTS_BY_PROJECT_ID_NOT_FOUND, projectId)));
    }

    private void validateDraft(PostDto postDto) {
        if ((postDto.getAuthorId() == null && postDto.getProjectId() == null) ||
                (postDto.getAuthorId() != null && postDto.getProjectId() != null)) {
            throw new PostValidationException(POSTS_MUST_HAVE_ONE_AUTHOR);
        }
    }

    private void validateUser(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException exception) {
            handleFeignException(exception, String.format(USER_WITH_ID_NOT_FOUND, userId));
        }
    }

    private void validateProject(Long projectId) {
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
        throw new PostValidationException(EXTERNAL_ERROR_MESSAGE + exception.getMessage());
    }

    private PostDto savePostAndMapToDto(Post post) {
        return postMapper.toDto(postRepository.save(post));
    }
}
