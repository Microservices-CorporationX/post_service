package ru.corporationx.postservice.mapper;

import ru.corporationx.postservice.dto.analytics.AnalyticsEventDto;
import ru.corporationx.postservice.dto.post.PostDto;
import ru.corporationx.postservice.model.event.EventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

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
