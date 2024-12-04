package faang.school.postservice.sheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${daily.cron.comment-moderation}")
    @Async("customTaskExecutor")
    public void startModeration() {
        commentService.verifyComments();
    }
}
