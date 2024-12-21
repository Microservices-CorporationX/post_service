package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumTitlePatternFilterTest extends SetUpFilterTest {
    private AlbumTitlePatternFilter albumTitlePatternFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        albumTitlePatternFilter = new AlbumTitlePatternFilter();
    }

    @Test
    public void testAlbumTitleFilterisApplicable() {
        assertTrue(albumTitlePatternFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumTitleFilterNotApplicable() {
        albumFilterDto.setTitlePattern(null);

        assertFalse(albumTitlePatternFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumTitleFilter() {
        List<Album> filteredAlbums = albumTitlePatternFilter.apply(albums, albumFilterDto).toList();

        assertEquals(1, filteredAlbums.size());
        assertEquals("Filter", filteredAlbums.get(0).getTitle());

    }
}
