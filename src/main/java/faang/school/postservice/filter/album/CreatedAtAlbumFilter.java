package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.album.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CreatedAtAlbumFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getCreatedAtPattern() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter) {
        return albums.filter(album -> album.getCreatedAt().isAfter(filter.getCreatedAtPattern()));
    }
}
