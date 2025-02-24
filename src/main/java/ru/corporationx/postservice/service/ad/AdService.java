package ru.corporationx.postservice.service.ad;


import ru.corporationx.postservice.dto.analytics.AnalyticsEventDto;
import ru.corporationx.postservice.model.event.EventType;
import ru.corporationx.postservice.publisher.ad.AdBoughtEventPublisher;
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
