package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/comment")
@RestController
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper mapper;

    @PostMapping("/create")
    public CommentDto createComment(@RequestBody @Valid CommentDto dto) {
        Comment rawComment = mapper.toEntity(dto);
        Comment result = commentService.createComment(rawComment, dto.getPostId());
        return mapper.toDto(result);
    }

    @PatchMapping("/update")
    public CommentDto updateComment(@RequestBody @Valid CommentDto dto) {
        Comment rawComment = mapper.toEntity(dto);
        Comment result = commentService.updateComment(rawComment);
        return mapper.toDto(result);
    }

    @GetMapping("/comments/{post-id}")
    public List<CommentDto> getAllCommentsToPost(@Valid @PathVariable("post-id") Long id) {
        List<Comment> comments = commentService.getAllCommentsToPost(id);
        return mapper.toDtoList(comments);
    }

    @DeleteMapping("/delete/{comment-id}")
    public void deleteComment(@Valid @PathVariable("comment-id") Long id) {
        commentService.deleteComment(id);
    }
}
