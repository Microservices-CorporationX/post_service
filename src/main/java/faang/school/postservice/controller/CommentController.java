package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId,
            @Valid @RequestBody CreateCommentDto dto) {
        log.info("Request for new commit for the post #{} from user #{}", postId, dto.getAuthorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, dto));
    }

    @PutMapping("/{postId}/comments")
    public CommentResponseDto update(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId,
            @Valid @RequestBody UpdateCommentDto dto) {
        log.info("Request for update of the comment #{} for the post #{}", dto.getId(), postId);
        return commentService.updateComment(postId, dto);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentResponseDto> getAllComments(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId) {
        log.info("Request for all comments for the post #{}", postId);
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId,
            @PathVariable @Positive(message = "Comment id should be a positive number") long commentId) {
        log.info("Request for delete of comment #{} from post #{} received", commentId, postId);
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }

}
