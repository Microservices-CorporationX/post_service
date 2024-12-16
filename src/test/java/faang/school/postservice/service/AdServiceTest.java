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
    @DisplayName("Get user who buy ad success")
    void testGetUserWhoBuyAdSuccess() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .build();

        Post post = new Post();
        post.setId(1L);

        Ad ad = new Ad();
        ad.setId(1L);
        ad.setPost(post);
        ad.setReceiverId(2L);
        ad.setPaymentAmount(new BigDecimal("100.00"));
        ad.setAdDuration(30);


        when(adRepository.findAllByBuyerId(userDto.getId())).thenReturn(List.of(ad));

        UserDto result = adService.getUserWhoBuyAd(userDto, 1L);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(adBoughtEventPublisher, times(1)).publish(any(AdBoughtEvent.class));
    }

    @Test
    @DisplayName("Get user who buy ad fail")
    void testGetUserWhoBuyAd_AdNotFound() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .build();

        String expectedMessage = "Ad with postId: 1 not found";

        when(adRepository.findAllByBuyerId(userDto.getId())).thenReturn(List.of());

        AdNotFoundException exception = assertThrows(AdNotFoundException.class, () -> {
            adService.getUserWhoBuyAd(userDto, 1L);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    private List<Ad> setUpExpiredAds() {
        return List.of(new Ad(), new Ad());
    }

    private List<List<Ad>> setUpBatches() {
        return List.of(setUpExpiredAds());
    }
}