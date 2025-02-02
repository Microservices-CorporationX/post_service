package faang.school.postservice.mapper.like;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.cache.LikeCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeCacheMapper {

    LikeCache toCache(Like like);
}
