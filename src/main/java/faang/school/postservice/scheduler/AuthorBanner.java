package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${post.auto_banner.scheduler.cron}")
    public void processUnverifiedPosts() {
        postService.checkAndBanAuthors();
    }
}