package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/comment")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/")
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        Long authorId = commentDto.getAuthorId();
        Long postId = commentDto.getPostId();

        Comment result = commentService.createComment(comment, postId, authorId);

        return ResponseEntity.ok(commentMapper.toDto(result));
    }

}
