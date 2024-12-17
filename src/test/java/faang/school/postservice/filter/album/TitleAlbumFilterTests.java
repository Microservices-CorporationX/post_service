package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TitleAlbumFilterTests {
    @InjectMocks
    private TitleAlbumFilter titleAlbumFilter;
    @Mock
    private AlbumFilterDto filterPatterns;
    private Album album;

    private Stream<Album> albums;

    @BeforeEach
    void setUp() {
        album = new Album();
        album.setId(1L);
        album.setTitle("Title");
        albums = Stream.of(album);

    }

    @Test
    public void testIsApplicableWithTitlePattern() {
        when(filterPatterns.getTitlePattern()).thenReturn("TestTitle");
        assertTrue(titleAlbumFilter.isApplicable(filterPatterns));
    }

    @Test
    void testIsApplicableWithoutTitlePattern() {
        when(filterPatterns.getTitlePattern()).thenReturn(null);
        assertFalse(titleAlbumFilter.isApplicable(filterPatterns));

        when(filterPatterns.getTitlePattern()).thenReturn("");
        assertFalse(titleAlbumFilter.isApplicable(filterPatterns));

        when(filterPatterns.getTitlePattern()).thenReturn("   ");
        assertFalse(titleAlbumFilter.isApplicable(filterPatterns));
    }

    @Test
    void testApplyForMatchingFilterPattern() {
        when(filterPatterns.getTitlePattern()).thenReturn("Title");
        Stream<Album> result = titleAlbumFilter.apply(albums, filterPatterns);
        assertEquals(album.getTitle(), result.toList().get(0).getTitle());
    }

    @Test
    void testApplyForNonMatchingFilterPattern() {
        when(filterPatterns.getTitlePattern()).thenReturn("TestTitle2");
        Stream<Album> result = titleAlbumFilter.apply(albums, filterPatterns);
        assertTrue(result.toList().isEmpty());
    }
}