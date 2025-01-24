package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(target = "comment", ignore = true)
    Like toEntity(LikeCommentDto dto);

    @Mapping(target = "post", ignore = true)
    Like toEntity(LikePostDto dto);

    @Mapping(source = "comment.id", target = "commentId")
    LikeCommentDto toCommentDto(Like entity);

    @Mapping(source = "post.id", target = "postId")
    LikePostDto toPostDto(Like entity);
}
