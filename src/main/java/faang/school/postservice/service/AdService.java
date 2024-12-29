package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.AdBoughtEvent;
import faang.school.postservice.exception.AdNotFoundException;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.publisher.AdBoughtEventPublisher;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.spliterator.Partitioner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private static final String ASYNC_CFG_EXECUTOR_BEAN_NAME = "taskExecutor";

    private final AdRepository adRepository;
    private final TransactionService transactionService;
    private final Partitioner<Ad> partitioner;
    private final AdBoughtEventPublisher adBoughtEventPublisher;


    public void getUserWhoBuyAd(Long postId, Long userId, BigDecimal paymentAmount, Long adDuration) {
        Optional<Ad> existingAd = adRepository.findByPostId(postId);

        existingAd.ifPresentOrElse(ad -> {
            AdBoughtEvent event = AdBoughtEvent.builder()
                    .postId(ad.getId())
                    .actorId(userId)
                    .paymentAmount(paymentAmount)
                    .adDuration(adDuration)
                    .receivedAt(LocalDateTime.now())
                    .build();

            adBoughtEventPublisher.publish(event);

        }, () -> {
            throw new AdNotFoundException(String.format("Ad with postId: %s not found", postId));
        });
    }


    @Async(ASYNC_CFG_EXECUTOR_BEAN_NAME)
    public void deleteAllExpiredAdsInBatches() {
        getExpiredAdsInBatches().forEach(this::processBatch);
    }

    private List<Ad> getExpiredAds() {
        return adRepository.findAllExpiredAds(LocalDate.now());
    }

    private List<List<Ad>> getExpiredAdsInBatches() {
        return partitioner.splitList(getExpiredAds());
    }

    private void processBatch(List<Ad> batch) {
        log.debug("Deletion of expired Ads in batch started");
        transactionService.deleteExpiredAdsInBatch(batch);
        log.debug("Deletion of expired Ads in batch finished");
    }
}
