package faang.school.postservice.config.schedule;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "0 * * * * ?")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
        log.info("The post has been published");
    }
}
