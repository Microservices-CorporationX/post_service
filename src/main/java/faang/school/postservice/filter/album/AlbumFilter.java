package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;

import java.util.stream.Stream;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto filter);

    Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter);
}
