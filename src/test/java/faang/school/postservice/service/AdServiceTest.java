package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.AdBoughtEvent;
import faang.school.postservice.exception.AdNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.publisher.AdBoughtEventPublisher;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.spliterator.Partitioner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AdBoughtEventPublisher adBoughtEventPublisher;

    @Mock
    private Partitioner<Ad> spliterator;

    @InjectMocks
    private AdService adService;

    @Test
    void shouldDeleteAllExpiredAdsInBatchesWhenThereAreExpiredAds() {
        when(adRepository.findAllExpiredAds(any(LocalDate.class))).thenReturn(setUpExpiredAds());
        when(spliterator.splitList(setUpExpiredAds())).thenReturn(setUpBatches());

        adService.deleteAllExpiredAdsInBatches();

        verify(transactionService, times(1)).deleteExpiredAdsInBatch(setUpExpiredAds());
    }

    @Test
    void shouldDoNothingWhenThereAreNoExpiredAds() {
        when(adRepository.findAllExpiredAds(any(LocalDate.class))).thenReturn(Collections.emptyList());

        adService.deleteAllExpiredAdsInBatches();

        verify(transactionService, times(0)).deleteExpiredAdsInBatch(any());
    }

    @Test
    @DisplayName("Get user who buys ad successfully")
    void testGetUserWhoBuyAdSuccess() {
        long postId = 1L;
        Long userId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100.00");
        long adDuration = 30L;

        Post post = new Post();
        post.setId(postId);

        Ad ad = new Ad();
        ad.setId(1L);
        ad.setPost(post);
        ad.setReceiverId(2L);
        ad.setPaymentAmount(paymentAmount);
        ad.setAdDuration(adDuration);

        when(adRepository.findByPostId(postId)).thenReturn(Optional.of(ad));

        assertDoesNotThrow(() -> adService.getUserWhoBuyAd(postId, userId, paymentAmount, adDuration));

        verify(adRepository, times(1)).findByPostId(postId);
        verify(adBoughtEventPublisher, times(1)).publish(any(AdBoughtEvent.class));
    }

    @Test
    @DisplayName("Throw AdNotFoundException when ad does not exist")
    void testGetUserWhoBuyAdAdNotFound() {
        long postId = 1L;
        Long userId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100.00");
        Long adDuration = 30L;

        when(adRepository.findByPostId(postId)).thenReturn(Optional.empty());

        AdNotFoundException exception = assertThrows(AdNotFoundException.class, () -> {
            adService.getUserWhoBuyAd(postId, userId, paymentAmount, adDuration);
        });

        assertEquals("Ad with postId: 1 not found", exception.getMessage());

        verify(adRepository, times(1)).findByPostId(postId);
        verifyNoInteractions(adBoughtEventPublisher);
    }

    private List<Ad> setUpExpiredAds() {
        return List.of(new Ad(), new Ad());
    }

    private List<List<Ad>> setUpBatches() {
        return List.of(setUpExpiredAds());
    }
}