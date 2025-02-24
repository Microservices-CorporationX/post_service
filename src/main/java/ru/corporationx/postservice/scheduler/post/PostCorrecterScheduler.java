package ru.corporationx.postservice.scheduler.post;

import ru.corporationx.postservice.config.context.UserContext;
import ru.corporationx.postservice.service.post.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecterScheduler {
    public static final Long DEFAULT_USER_ID = 1L;
    public static final int MAX_ATTEMPTS = 5;

    private final PostService postService;
    private final UserContext userContext;

    @Async("postCorrectorThreadPool")
    @Scheduled(cron = "${post-correction.cron:0 0 0 * * *}")
    @Transactional
    @Retryable(retryFor = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void correctContentOfUnpublishedPosts() {
        userContext.setUserId(DEFAULT_USER_ID);
        postService.checkText();
    }

    @Recover
    public void recover(FeignException e) {
        log.error("All attempts to access the grammar check api have been unsuccessful. Attempts made: {}",
                MAX_ATTEMPTS, e);
    }
}
