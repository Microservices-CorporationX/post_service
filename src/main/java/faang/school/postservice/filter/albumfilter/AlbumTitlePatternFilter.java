package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitlePatternFilter implements Filter<Album, AlbumFilterDto> {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter) {
        return albums.filter(album ->
                album.getTitle().toLowerCase().contains(filter.getTitlePattern().toLowerCase()));
    }
}
