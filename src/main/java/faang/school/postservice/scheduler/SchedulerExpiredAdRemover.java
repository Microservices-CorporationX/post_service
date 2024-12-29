package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${schedule.cron.deleteExpiredAds}")
    public void deleteExpiredAds() {
        adService.deleteAllExpiredAdsInBatches();
    }
}
