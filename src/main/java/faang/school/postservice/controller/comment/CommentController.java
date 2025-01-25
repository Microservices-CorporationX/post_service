package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper mapper;

    @PostMapping("/comments")
    public CommentDto createComment(@RequestBody @Valid CommentDto dto) {
        Comment rawComment = mapper.toEntity(dto);
        Comment result = commentService.createComment(rawComment, dto.getPostId());
        return mapper.toDto(result);
    }

    @PatchMapping("/comments")
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto dto) {
        Comment rawComment = mapper.toEntity(dto);
        Comment result = commentService.updateComment(rawComment);
        return mapper.toDto(result);
    }

    @GetMapping("/comments/{postId}")
    public List<CommentDto> getAllCommentsToPost(@Positive @PathVariable long postId) {
        List<Comment> comments = commentService.getAllCommentsToPost(postId);
        return mapper.toDtoList(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@Positive @PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }
}