package faang.school.postservice.service.implementation;

import faang.school.postservice.Exception.DataNotFoundException;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_FOUND = "Пост не найден";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Override
    public PostResponseDto create(CreatePostDto createPostDto) {
        postValidator.validateDraftPost(createPostDto);

        Post post = postMapper.toEntity(createPostDto);
        post.setPublished(false);

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    @Override
    public PostResponseDto getPost(long postId) {
        Post post = findPostById(postId);
        if (post.isDeleted()) {
            throw new DataNotFoundException(POST_NOT_FOUND);
        }
        return postMapper.toDto(post);
    }

    @Override
    public PostResponseDto update(long postId, UpdatePostDto updatePostDto) {
        Post post = findPostById(postId);

        post.setContent(updatePostDto.content());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    @Override
    public PostResponseDto delete(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND + " with id: " + id));
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }

    @Override
    public PostResponseDto publish(long id) {
        Post post = findPostById(id);

        postValidator.validateNotPublished(post);
        postValidator.validateNotDeleted(post);
        postValidator.validatePostAuthorExist(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    private Post findPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND));
    }

    @Override
    public List<PostResponseDto> getDraftPostsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toDtoList(posts);
    }

    @Override
    public List<PostResponseDto> getDraftPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toDtoList(posts);
    }

    @Override
    public List<PostResponseDto> getPublishedPostsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
        return postMapper.toDtoList(posts);
    }

    @Override
    public List<PostResponseDto> getPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
        return postMapper.toDtoList(posts);
    }
}