package faang.school.postservice.service.implementation;

import faang.school.postservice.Exception.DataNotFoundException;
import faang.school.postservice.Exception.DataValidationException;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.SavePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_FOUND = "Пост не найден";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Override
    public PostDto create(SavePostDto savePostDto) {
        Post post = postMapper.toEntity(savePostDto);
        postValidator.validateDraftPost(savePostDto);
        post.setPublished(false);
        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    @Override
    public PostDto getPost(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND));
        postValidator.validatePostAuthorExist(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto update(long id, SavePostDto savePostDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND));

        if (!Objects.equals(existingPost.getAuthorId(), savePostDto.getAuthorId()) ||
                !Objects.equals(existingPost.getProjectId(), savePostDto.getProjectId())) {
            throw new DataValidationException("Нельзя изменить автора поста");
        }

        existingPost.setContent(savePostDto.getContent());

        Post updatedPost = postRepository.save(existingPost);

        return postMapper.toDto(updatedPost);
    }

    @Override
    public PostDto delete(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND + " with id: " + id));
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }

    @Override
    public PostDto publish(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND));

        postValidator.validateNotPublished(post);
        postValidator.validateNotDeleted(post);
        postValidator.validatePostAuthorExist(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    @Override
    public List<PostDto> getDraftPostsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getPublishedPostsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getDraftPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return postMapper.toDto(posts);
    }
}