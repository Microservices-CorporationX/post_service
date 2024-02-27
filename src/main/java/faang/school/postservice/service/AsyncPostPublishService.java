package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncPostPublishService {
    private final PostRepository postRepository;

    @Async("executorService")
    public void publishPost(List<Post> posts) {
        log.info("Started async publish sublist of size {}", posts.size());
        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(posts);
        log.info("Finished publish {} post(s)", posts.size());
    }
}