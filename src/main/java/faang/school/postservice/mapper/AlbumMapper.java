package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    AlbumDto toDto(Album album);
    Album toEntity(AlbumDto albumDto);
    List<AlbumDto> toDto(List<Album> album);
    void update(AlbumDto albumDto, @MappingTarget Album album);
}
