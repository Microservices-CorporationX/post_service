package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumCreatedDateFilter implements AlbumFilter{

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getCreatedAt() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
        return albums.filter(album -> album.getCreatedAt().isAfter(filters.getCreatedAt()) ||
                album.getCreatedAt().equals(filters.getCreatedAt()));
    }
}
