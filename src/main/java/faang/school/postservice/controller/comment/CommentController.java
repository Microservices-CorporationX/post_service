package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper mapper;

    @PostMapping("/comment")
    public CommentDto createComment(@RequestBody CommentDto dto) {
        Comment rawComment = mapper.toEntity(dto);
        Comment result = commentService.createComment(rawComment, dto.getPostId());
        return mapper.toDto(result);
    }
}
