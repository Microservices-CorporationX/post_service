package ru.corporationx.postservice.filters.album;

import ru.corporationx.postservice.dto.album.AlbumFilterDto;
import ru.corporationx.postservice.model.Album;

import java.util.stream.Stream;

public interface AlbumFilter {

    boolean isApplicable(AlbumFilterDto filters);

    Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filters);
}
