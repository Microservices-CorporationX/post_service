package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersBanScheduler {
    private final CommentService commentService;

    @Scheduled(cron = "${daily.cron.user-ban}")
    public void banCommenters() {
        System.out.println(Thread.currentThread().getName());
        commentService.publishUsersToBanEvent();
    }
}
