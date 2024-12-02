package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Bean
    public ExecutorService moderationPool() {
        return Executors.newFixedThreadPool(5);
    }

    @Value("${moderation-scheduler.post-batch-size}")
    private int postBatchSize;

    @Scheduled(cron = "${moderation-scheduler.cron}")
    public void moderateContent() {
        List<Post> allPostsWithVerifiedIsNull = postRepository.findByVerifiedIsNull();
        int totalSizePosts = allPostsWithVerifiedIsNull.size();

        if (totalSizePosts > 0) {
            for (int i = 0; i < totalSizePosts; i += postBatchSize) {
                int end = Math.min(i + postBatchSize, totalSizePosts);
                List<Post> batch = allPostsWithVerifiedIsNull.subList(i, end);

                log.info("Batch has been received");
                processBatchAsync(batch);
            }
        }
        log.info("Moderation is complete");
    }

    @Async("moderationConfig")
    public void processBatchAsync(List<Post> batch) {
        batch.forEach(post -> {
            post.setVerifiedDate(LocalDateTime.now());
            boolean isVerified = moderationDictionary.isVerified(post.getContent());
            post.setVerified(isVerified);
            log.debug("Post with id {} has been verified and has status {}", post.getId(), isVerified);

            postRepository.save(post);
        });
    }
}
