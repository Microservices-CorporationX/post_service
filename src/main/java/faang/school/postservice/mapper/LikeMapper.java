package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "createdAt", target = "createdAt")
    LikeDto toDto(Like like);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "comment",ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Like toEntity(LikeDto likeDto);
}
