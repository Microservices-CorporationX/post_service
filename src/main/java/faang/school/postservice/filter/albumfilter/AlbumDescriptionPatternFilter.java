package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class AlbumDescriptionPatternFilter implements Filter<Album,AlbumFilterDto> {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter){
        return albums.filter(album ->
                album.getDescription().toLowerCase().contains(filter.getDescriptionPattern().toLowerCase()));
    }
}
