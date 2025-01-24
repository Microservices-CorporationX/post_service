package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post findById(@NotNull Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пост с id=%d не найден", id)));
    }

    public List<Post> findByProjectIdWithLikes(@NotNull Long projectId) {
        return postRepository.findByProjectIdWithLikes(projectId);
    }

    public List<Post> findByAuthorIdWithLikes(@NotNull Long userId) {
        return postRepository.findByAuthorIdWithLikes(userId);
    }

    public boolean existsById(@NotNull Long postId) {
        return postRepository.existsById(postId);
    }
}
