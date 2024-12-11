package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AdRepository adRepository;

    @Test
    public void testDeleteExpiredAdsInBatchWithEmptyList() {
        List<Ad> ads = Collections.emptyList();

        transactionService.deleteExpiredAdsInBatch(ads);

        verify(adRepository, never()).deleteAllInBatch(anyList());
    }

    @Test
    public void testDeleteExpiredAdsInBatchWithNonEmptyList() {
        List<Ad> ads = Collections.singletonList(setUpAd(1L));

        transactionService.deleteExpiredAdsInBatch(ads);

        verify(adRepository, times(1)).deleteAllInBatch(ads);
    }

    @Test
    public void testDeleteExpiredAdsInBatchWithMultipleAds() {
        List<Ad> ads = List.of(setUpAd(1L), setUpAd(2L));

        transactionService.deleteExpiredAdsInBatch(ads);

        verify(adRepository, times(1)).deleteAllInBatch(ads);
    }

    private Ad setUpAd(long id) {
        return Ad.builder().id(id).build();
    }
}