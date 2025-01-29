package faang.school.postservice.mapper;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {
    CommentEvent toDto(Comment comment);
}
