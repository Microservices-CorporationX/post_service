package faang.school.postservice.service.ad;


import faang.school.postservice.dto.analytics.AnalyticsEventDto;
import faang.school.postservice.model.event.EventType;
import faang.school.postservice.publisher.ad.AdBoughtEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdBoughtEventPublisher adBoughtEventPublisher;

    public void buyAdd(long postId) {
        AnalyticsEventDto adBoughtEvent =
                new AnalyticsEventDto(1L, 1L, EventType.AD_BOUGHT_EVENT.ordinal(), LocalDateTime.now());
        adBoughtEventPublisher.publish(adBoughtEvent);
    }
}
