package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    //@Mapping(target = "skills", ignore = true)
    //@Mapping(target = "requester", ignore = true)
    //@Mapping(target = "receiver", ignore = true)
    Comment toCommentEntity(CommentRequestDto commentRequestDto);

    //@Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkills")
    //@Mapping(source = "requester.id", target = "requesterId")
    //@Mapping(source = "receiver.id", target = "receiverId")
    CommentResponseDto toCommentResponseDto(Comment comment);

    /*
    @Named("mapSkills")
    default List<Long> mapSkills(List<SkillRequest> skills) {
        if (skills != null) {
            return skills.stream()
                    .map(SkillRequest::getId)
                    .toList();
        }
        return new ArrayList<>();
    } */
}
