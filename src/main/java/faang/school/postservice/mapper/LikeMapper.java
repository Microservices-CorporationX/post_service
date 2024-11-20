package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    Like toLike(LikeDto likeDto);

    LikeDto toLikeDto(Like like);

    @Mapping(target = "userId", source = "authorLikeId")
    LikeDto toLikeDto(FeedEventProto.FeedEvent feedEvent);
}
