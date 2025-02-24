package ru.corporationx.postservice.filters.album;

import ru.corporationx.postservice.dto.album.AlbumFilterDto;
import ru.corporationx.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter implements AlbumFilter{

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
       return albums.filter(album -> album.getTitle().toLowerCase().contains(filters.getTitle().toLowerCase()));
    }
}
