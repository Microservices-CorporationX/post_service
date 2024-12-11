package faang.school.postservice.scheduler;

import faang.school.postservice.event.AdBoughtEvent;
import faang.school.postservice.publisher.AdBoughtEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdBoughtScheduler {
    private final AdBoughtEventPublisher adBoughtEventPublisher;

    @Scheduled(cron = "*/10 * * * * *")
    public void sendAdBoughtEvent() {
        AdBoughtEvent adBoughtEvent = AdBoughtEvent.builder()
                .actorId(1L)
                .receiverId(2L)
                .adDuration(30L)
                .paymentAmount(BigDecimal.valueOf(100))
                .receivedAt(LocalDateTime.now())
                .postId(1L)
                .build();
        adBoughtEventPublisher.publish(adBoughtEvent);
    }
}
