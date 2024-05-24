package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {

    public boolean isAcceptable(AlbumFilterDto albumFilterDto);

    public Stream<Album> applyFilter(Stream<Album> albums, AlbumFilterDto albumFilterDto);

}