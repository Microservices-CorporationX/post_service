package faang.school.postservice.mapper;

import faang.school.postservice.dto.analytics.AnalyticsEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.event.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostViewEventMapper {
    @Mapping(target = "receiverId", source = "postDto.id")
    @Mapping(target = "eventTypeNumber", expression = "java(getPostViewNumber())")
    @Mapping(target = "receivedAt", expression = "java(java.time.LocalDateTime.now())")
    AnalyticsEventDto toAnalyticsEventDto(PostDto postDto, Long actorId);

    default int getPostViewNumber() {
        return EventType.POST_VIEW.ordinal();
    }
}
