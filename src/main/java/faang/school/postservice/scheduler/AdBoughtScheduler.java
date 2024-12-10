package faang.school.postservice.scheduler;

import faang.school.postservice.dto.AdBoughtEvent;
import faang.school.postservice.publisher.AdBoughtEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AdBoughtScheduler {
    private final AdBoughtEventPublisher adBoughtEventPublisher;

    @Scheduled(cron = "*/20 * * * * *")
    public void sendAdBoughtEvent() {
        AdBoughtEvent adBoughtEvent = AdBoughtEvent.builder()
                .actorId(1L)
                .adDuration(30L)
                .paymentAmount(BigDecimal.valueOf(100))
                .postId(1L)
                .build();
        adBoughtEventPublisher.publish(adBoughtEvent);
    }
}
