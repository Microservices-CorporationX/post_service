package faang.school.postservice.service.post;

import faang.school.postservice.exeption.PostAlreadyPublishedException;
import faang.school.postservice.exeption.PostWasDeletedException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void createPostByUserId(Long userId, Post post) {
        post.setAuthorId(userId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional
    public void createPostByProjectId(Long projectId, Post post) {
        post.setProjectId(projectId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional
    public void publishPost(Long postId) {
        Post post = getPost(postId);

        if (post.getPublishedAt() == null) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        } else {
            throw new PostAlreadyPublishedException("The post has already been published");
        }
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long postId, Post post) {
        Post existingPost = getPost(postId);

        existingPost.setContent(post.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());
        existingPost.setProjectId(post.getProjectId());

        postRepository.save(existingPost);
    }

    @Transactional
    public void softDeletePost(Long postId) {
        Post existingPost = getPost(postId);

        existingPost.setDeleted(true);

        postRepository.save(existingPost);
    }

    public Post getPostById(Long postId) {
        Post post = getPost(postId);

        if (!post.isDeleted()) {
            return post;
        } else {
            throw new PostWasDeletedException("The post was deleted");
        }
    }

    public List<Post> getNotPublishedPostsByUser(Long userId) {
        return postRepository
                .findByAuthorIdWithLikes(userId)
                .stream().filter(post -> !post.isPublished())
                .collect(Collectors.toList());
    }

    public List<Post> getNotPublishedPostsByProject(Long projectId) {
        return postRepository
                .findByProjectIdWithLikes(projectId)
                .stream().filter(post -> !post.isPublished())
                .collect(Collectors.toList());

    }

    public List<Post> getPublishedPostsByUser(Long userId) {
        return postRepository
                .findByAuthorIdWithLikes(userId)
                .stream().filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    public List<Post> getPublishedPostsByProject(Long projectId) {
        return postRepository
                .findByProjectIdWithLikes(projectId)
                .stream().filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }
}