package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    protected Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));
    }
}
