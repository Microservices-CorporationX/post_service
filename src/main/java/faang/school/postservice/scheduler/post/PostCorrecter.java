package faang.school.postservice.scheduler.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostCorrecter {
    public static final Long DEFAULT_USER_ID = 1L;

    private final PostService postService;
    private final UserContext userContext;

    @Async("postCorrectorThreadPool")
    @Scheduled(cron = "${post-correction.cron}")
    @Transactional
    @Retryable(retryFor = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void correctContentOfUnpublishedPosts() {
        userContext.setUserId(DEFAULT_USER_ID);
        postService.checkText();
    }
}
