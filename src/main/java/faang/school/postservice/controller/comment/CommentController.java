package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto createComment(@RequestBody @Valid CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable @NotNull Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable @NotNull Long commentId,
                              @RequestParam @NotNull Long authorId,
                              @RequestBody @Valid CommentUpdateDto commentUpdateDto) {
        commentService.updateComment(commentId, authorId, commentUpdateDto);
    }

    @GetMapping
    public List<CommentResponseDto> getCommentsByFilters(@RequestBody @Valid CommentFiltersDto commentFiltersDto) {
        return commentService.getComments(commentFiltersDto);
    }
}
