package faang.school.postservice.scheduler.post;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${post.publish-post.schedule.cron}")
    public void publishPosts() {
        postService.publishScheduledPosts();
    }
}
