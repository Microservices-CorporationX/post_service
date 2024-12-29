package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    @Mapping(source = "posts", target = "postsId", qualifiedByName = "mapToPostsId")
    AlbumDto toDto(Album album);

    @Mapping(target = "posts", ignore = true)
    Album toEntity(AlbumDto albumDto);

    @Mapping(target = "authorId", ignore = true)
    void update(AlbumUpdateDto albumUpdateDto, @MappingTarget Album album);

    @Named("mapToPostsId")
    default List<Long> mapToPostsId(List<Post> posts) {
        if (posts == null) {
            return new ArrayList<>();
        }
        return posts.stream()
                .map(Post::getId)
                .toList();
    }
}
