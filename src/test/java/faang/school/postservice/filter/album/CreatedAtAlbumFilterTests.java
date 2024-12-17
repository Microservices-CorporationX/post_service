package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatedAtAlbumFilterTests {
    @InjectMocks
    private CreatedAtAlbumFilter createdAtAlbumFilter;
    @Mock
    private AlbumFilterDto filterPatterns;
    private Album album;
    private LocalDateTime createdAt;

    private Stream<Album> albums;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now();
        album = new Album();
        album.setId(1L);
        album.setTitle("Title");
        album.setCreatedAt(createdAt);
        albums = Stream.of(album);
    }

    @Test
    public void testIsApplicableWithDatePattern() {
        when(filterPatterns.getCreatedAtPattern()).thenReturn(LocalDateTime.now());
        assertTrue(createdAtAlbumFilter.isApplicable(filterPatterns));
    }

    @Test
    void testIsApplicableWithoutDatePattern() {
        when(filterPatterns.getCreatedAtPattern()).thenReturn(null);
        assertFalse(createdAtAlbumFilter.isApplicable(filterPatterns));
    }

    @Test
    void testApplyForMatchingFilterPattern() {
        when(filterPatterns.getCreatedAtPattern()).thenReturn(createdAt.plusDays(1));
        Stream<Album> result = createdAtAlbumFilter.apply(albums, filterPatterns);
        assertTrue(result.toList().isEmpty());
    }

    @Test
    void testApplyForNonMatchingFilterPattern() {
        when(filterPatterns.getCreatedAtPattern()).thenReturn(createdAt.minusDays(1));
        Stream<Album> result = createdAtAlbumFilter.apply(albums, filterPatterns);
        assertEquals(album.getTitle(), result.toList().get(0).getTitle());
    }
}