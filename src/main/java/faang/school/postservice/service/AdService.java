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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private static final String ASYNC_CFG_EXECUTOR_BEAN_NAME = "taskExecutor";

    private final AdRepository adRepository;
    private final TransactionService transactionService;
    private final Partitioner<Ad> partitioner;
    private final AdBoughtEventPublisher adBoughtEventPublisher;


    public UserDto getUserWhoBuyAd(UserDto userDto, long postId) {
        List<Ad> ads = adRepository.findAllByBuyerId(userDto.getId());

        return ads.stream()
                .filter(ad -> ad.getPost().getId() == postId)
                .findFirst()
                .map(ad -> {
                    AdBoughtEvent event = AdBoughtEvent.builder()
                            .postId(ad.getId())
                            .actorId(userDto.getId())
                            .receiverId(ad.getReceiverId())
                            .paymentAmount(ad.getPaymentAmount())
                            .adDuration(ad.getAdDuration())
                            .receivedAt(LocalDateTime.now())
                            .build();

                    adBoughtEventPublisher.publish(event);

                    return UserDto.builder()
                            .id(userDto.getId())
                            .username(userDto.getUsername())
                            .email(userDto.getEmail())
                            .build();
                })
                .orElseThrow(() -> new AdNotFoundException(String.format("Ad with postId: %s not found", postId)));

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
