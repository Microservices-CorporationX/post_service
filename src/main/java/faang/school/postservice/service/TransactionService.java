package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AdRepository adRepository;

    @Transactional
    public void deleteExpiredAdsInBatch(List<Ad> ads) {
        if (!ads.isEmpty()) {
            adRepository.deleteAllInBatch(ads);
        }
    }

}
