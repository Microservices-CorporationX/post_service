package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumDescriptionPatternFilterTest extends SetUpFilterTest {
    private AlbumDescriptionPatternFilter albumDescriptionPatternFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        albumDescriptionPatternFilter = new AlbumDescriptionPatternFilter();
    }

    @Test
    public void testAlbumDescriptionFilterIsApplicable() {
        assertTrue(albumDescriptionPatternFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumDescriptionFilterNotApplicable() {
        albumFilterDto.setDescriptionPattern(null);
        assertFalse(albumDescriptionPatternFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumDescriptionFilter() {
        List <Album>  filteredAlbums = albumDescriptionPatternFilter.apply(albums,albumFilterDto).toList();

        assertEquals(2,filteredAlbums.size());
        assertEquals("Java the best",filteredAlbums.get(0).getDescription());
        assertEquals("Java the best",filteredAlbums.get(1).getDescription());
    }
}
