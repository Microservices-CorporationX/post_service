package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper mapper;

    public CommentDto createComment(CommentDto dto) {
        Comment rawComment = mapper.toEntity(dto);
        commentService.createComment(rawComment, dto.getPostId());
        return dto;
    }
}
