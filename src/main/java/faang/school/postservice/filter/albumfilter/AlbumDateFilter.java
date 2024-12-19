package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumDateFilter implements Filter<Album, AlbumFilterDto> {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getMonth() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter){
        return albums.filter(album -> album.getCreatedAt().getMonth().equals(filter.getMonth()));
    }
}
