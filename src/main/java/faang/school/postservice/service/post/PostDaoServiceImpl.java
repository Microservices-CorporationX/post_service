package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDaoServiceImpl implements PostDaoService {

    private final PostRepository repository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public List<Post> findAll() {
        Iterable<Post> iterablePosts = repository.findAll();
        List<Post> posts = iterableToList(iterablePosts);
        if (posts.isEmpty()) {
            log.info("No elements found!");
        }
        return MapperUtil.convertList(posts, Post.class);
    }

    @Override
    public Post getById(Long id) {
        return repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Post create(Post post) {
        isPostAuthorExist(post);
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        post.setDeleted(false);
        return repository.save(post);
    }

    @Override
    @Transactional
    public Post publish(Long id) {
        Post post = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isPublished()) {
            throw new PostException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return repository.save(post);
    }

    @Override
    public Post update(Post postDto) {
        Post post = repository.findById(postDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(postDto.getContent());
        return repository.save(post);
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        return repository.findByIdAndNotDeleted(id).orElseThrow(() -> new ResourceNotFoundException("Post Not Found with ID = " + id));
    }

    @Override
    public Post deleteById(Long id) {
        Post post = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if (post.isDeleted()) {
            throw new PostException("post already deleted");
        }
        post.setPublished(false);
        post.setDeleted(true);
        return repository.save(post);
    }

    public List<Post> filterNonPublishedPostsByTime(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Post> getAllNonPublishedByAuthorId(Long id) {
        validateUserExist(id);
        return filterNonPublishedPostsByTime(repository.findByAuthorId(id));
    }

    private void validateUserExist(Long id) {
        userServiceClient.getUser(id);
    }

    private void validateProjectExist(Long id) {
        projectServiceClient.getProject(id);
    }

    private void isPostAuthorExist(Post post) {
        if (post.getAuthorId() != null) {
            userServiceClient.getUser(post.getAuthorId());
        } else {
            projectServiceClient.getProject(post.getProjectId());
        }
    }

    public static <T> List<T> iterableToList(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }
}
