package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
                                    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateCommentMapper {

    Comment toEntity(UpdateCommentDto dto);
}