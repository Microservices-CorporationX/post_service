package ru.corporationx.postservice.service.ad;

import ru.corporationx.postservice.publisher.ad.AdBoughtEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.postservice.service.ad.AdService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {
    @Mock
    private AdBoughtEventPublisher adBoughtEventPublisher;
    @InjectMocks
    private AdService adService;

    @Test
    void testBuyAd() {
        adService.buyAdd(1L);
        verify(adBoughtEventPublisher).publish(any());
    }
}