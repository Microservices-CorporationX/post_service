package faang.school.postservice.config.schedule;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${post.ban-authors.scheduler.cron}")
    public void banAuthorsWithOffensiveContent() {
        postService.banOffensiveAuthors();
    }
}
