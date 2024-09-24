package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class PostPublisher {

    private final PostRepository postRepository;

    @Transactional
    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(posts);
        log.debug("Published {} posts", posts.size());
    }
}
