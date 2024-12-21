package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdServiceImplTest {
    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceImpl adService;

    @Test
    void shouldThrowExceptionIfAdsListIsNull() {
        List<Ad> ads = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adService.deleteAds(ads);
        });
        assertEquals("Список объявлений не может быть null или пустым", exception.getMessage());
        verify(adRepository, times(0)).deleteAll(anyList());
    }

    @Test
    void shouldThrowExceptionIfAdsListIsEmpty() {
        List<Ad> ads = List.of();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adService.deleteAds(ads);
        });
        assertEquals("Список объявлений не может быть null или пустым", exception.getMessage());
        verify(adRepository, times(0)).deleteAll(anyList());
    }

    @Test
    void shouldDeleteAdsSuccessfully() {
        List<Ad> ads = List.of(Ad.builder().build());
        doNothing().when(adRepository).deleteAll(anyList());

        adService.deleteAds(ads);

        InOrder inOrder = inOrder(adRepository);
        inOrder.verify(adRepository, times(1)).deleteAll(ads);
    }

    @Test
    void shouldThrowRuntimeExceptionIfDataAccessExceptionOccurs() {
        List<Ad> ads = List.of(Ad.builder().build());
        doThrow(new DataAccessException("Ошибка доступа") {}).when(adRepository).deleteAll(anyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adService.deleteAds(ads);
        });
        assertEquals("Не удалось удалить объявления", exception.getMessage());
    }
}
