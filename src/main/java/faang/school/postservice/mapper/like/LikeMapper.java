package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.model.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "post.id", target = "postId")
    ResponseLikeDto toLikeDtoFromEntity(Like like);
}
