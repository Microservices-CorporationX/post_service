package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }
}
