package faang.school.postservice.service.ad;

import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    public List<Long> findExpiredAdIds() {
        List<Long> expiredIds = new ArrayList<>();
        adRepository.findAll()
                .forEach(ad -> {
                    if (ad.getAppearancesLeft() < 1 || ad.getEndDate().isBefore(LocalDateTime.now())) {
                        expiredIds.add(ad.getId());
                    }
                });
        return expiredIds;
    }

    public void deleteAdByIds(List<Long> ids) {
        adRepository.deleteAllById(ids);
    }
}
