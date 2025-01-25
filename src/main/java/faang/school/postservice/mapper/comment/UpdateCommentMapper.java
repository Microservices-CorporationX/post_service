package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

public interface UpdateCommentMapper {
    @Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
            unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Comment toDto(UpdateCommentDto dto);
}
