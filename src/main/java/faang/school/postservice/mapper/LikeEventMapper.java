package faang.school.postservice.mapper;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeEventMapper {
    LikeEvent toDto(Like like);
}
