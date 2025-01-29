package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
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
    Like toEntity(LikeDto likeDto);
}
