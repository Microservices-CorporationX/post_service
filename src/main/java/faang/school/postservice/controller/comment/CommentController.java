package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController implements CommentControllerOas {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public CommentDto createComment(@PathVariable @Positive long postId, @RequestBody @Valid CommentDto comment) {
        if (comment.getId() != null) {
            throw new DataValidationException("The comment must not contain an ID for creation");
        }
        if (postId != comment.getPostId()) {
            throw new DataValidationException("Path postId and body postId are different");
        }

        return commentService.createComment(comment);
    }

    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive long commentId,@RequestBody @Valid CommentDto comment) {
        if (commentId != comment.getId()) {
            throw new DataValidationException("Path commentId and body commentId are different");
        }
        return commentService.updateComment(comment);
    }

    @GetMapping("/{postId}")
    public List<CommentDto> getComments(@PathVariable @Positive long postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable @Positive long commentId) {
        commentService.deleteComment(commentId);
    }

}
