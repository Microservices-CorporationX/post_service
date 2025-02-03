package faang.school.postservice.service.album.filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.titlePattern() != null && !filter.titlePattern().isBlank();
    }

    @Override
    public Stream<Album> apply(Stream<Album> albumStream, AlbumFilterDto filter) {
        return albumStream.filter(album -> album.getTitle().matches(filter.titlePattern()));
    }
}
