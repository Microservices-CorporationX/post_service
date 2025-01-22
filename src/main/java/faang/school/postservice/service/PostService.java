package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostDto createPostDraft(PostCreateDto dto) {
        var authorId = dto.getAuthorId();
        var projectId = dto.getProjectId();

        if (authorId != null && userServiceClient.getUser(authorId) == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        } else if (projectId != null && projectServiceClient.getProject(projectId) == null) {
            throw new EntityNotFoundException("Проект не найден");
        }

        Post post = postMapper.toEntity(dto);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto publishPost(long id) {
        Post post = getPostById(id);
        if (post.isPublished()) {
            throw new BusinessException("Пост уже опубликован");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto updatePost(long id, PostUpdateDto dto) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост удалён");
        }
        postMapper.updateEntityFromDto(dto, post);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto softDeletePost(long id) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост уже удален");
        }
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostDto> getAllDraftsByAuthor(long authorId) {
        return postRepository.findAllDraftsByAuthorId(authorId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getAllDraftsByProject(long projectId) {
        return postRepository.findAllDraftsByProjectId(projectId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getAllPublishedPostsByAuthor(long authorId) {
        return postRepository.findAllPublishedByAuthorId(authorId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getAllPublishedPostsByProject(long projectId) {
        return postRepository.findAllPublishedByProjectId(projectId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public Post getPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));
    }
}
