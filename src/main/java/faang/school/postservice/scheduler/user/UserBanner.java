package faang.school.postservice.scheduler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanner {

    private final UserBanner userBanner;

    @Async("userBannerThreadPool")
    @Transactional
    @Scheduled(cron = "${banner.cron}", zone = "${banner.zone}")
    public void banUsersScheduler() {
        log.info("user banner scheduler is starting!");
        userBanner.banUsersScheduler();
        log.info("user banner scheduler is success finished!");
    }
}
