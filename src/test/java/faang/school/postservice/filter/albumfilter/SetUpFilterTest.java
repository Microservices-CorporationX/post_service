package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.stream.Stream;

public class SetUpFilterTest {
    protected AlbumFilterDto albumFilterDto;
    protected Stream<Album> albums;

    @BeforeEach
    void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("Filter")
                .descriptionPattern("Java the best")
                .month(Month.MARCH)
                .build();
        Album album1 = Album.builder()
                .id(1L)
                .title("Filter")
                .description("Not content")
                .createdAt(LocalDateTime.of(2023, Month.MARCH, 10, 12, 0))
                .build();
        Album album2 = Album.builder()
                .id(2L)
                .title("Title1")
                .description("Java the best")
                .createdAt(LocalDateTime.of(2023, Month.APRIL, 10, 12, 0))
                .build();
        Album album3 = Album.builder()
                .id(3L)
                .title("Title2")
                .description("Java the best")
                .createdAt(LocalDateTime.of(2023, Month.JANUARY, 10, 12, 0))
                .build();

        albums = Stream.of(album1, album2, album3);
    }
}
