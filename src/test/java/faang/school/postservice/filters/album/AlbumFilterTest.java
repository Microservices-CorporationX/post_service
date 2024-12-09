package faang.school.postservice.filters.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class AlbumFilterTest {

    @Spy
    private AlbumTitleFilter titleFilter;
    @Spy
    private AlbumCreatedDateFilter createdDateFilter;


    @Test
    public void testAlbumTitleFilterNotApplicable() {
        AlbumFilterDto filterDto = AlbumFilterDto.builder().build();

        boolean result = titleFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void TestApplyTitleFilter() {
        AlbumFilterDto filters = AlbumFilterDto.builder()
                .title("title")
                .build();

        Album first = new Album();
        first.setTitle("title");

        Album second = new Album();
        second.setTitle("second");

        List<Album> albums = new ArrayList<>(List.of(first, second));

        List<Album> result = titleFilter.apply(albums.stream(), filters).toList();

        assertEquals("title", result.get(0).getTitle());
    }

    @Test
    public void TestApplyDateFilterOnePositive() {
        AlbumFilterDto filters = AlbumFilterDto.builder()
                .title("title")
                .createdAt(LocalDateTime.of(2000, 10, 1, 0, 0))
                .build();

        Album first = new Album();
        first.setTitle("title");
        first.setCreatedAt(LocalDateTime.of(2000, 11, 1, 0, 0));

        Album second = new Album();
        second.setTitle("second");
        second.setCreatedAt(LocalDateTime.of(2000, 9, 1, 0, 0));

        List<Album> albums = new ArrayList<>(List.of(first, second));

        List<Album> result = createdDateFilter.apply(albums.stream(), filters).toList();

        assertEquals("title", result.get(0).getTitle());
        assertEquals(1, result.size());
    }

    @Test
    public void TestApplyDateFilterTwoPositive() {
        AlbumFilterDto filters = AlbumFilterDto.builder()
                .title("title")
                .createdAt(LocalDateTime.of(2000, 10, 1, 0, 0))
                .build();

        Album first = new Album();
        first.setTitle("title");
        first.setCreatedAt(LocalDateTime.of(2000, 11, 1, 0, 0));

        Album second = new Album();
        second.setTitle("second");
        second.setCreatedAt(LocalDateTime.of(2000, 12, 1, 0, 0));

        List<Album> albums = new ArrayList<>(List.of(first, second));

        List<Album> result = createdDateFilter.apply(albums.stream(), filters).toList();

        assertEquals(2, result.size());
    }
}
