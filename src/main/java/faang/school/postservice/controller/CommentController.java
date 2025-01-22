package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/comments")
    public CommentReadDto create(@Valid @RequestBody CommentCreateDto createDto) {
        return commentService.create(createDto);
    }

    @PutMapping("/posts/comments")
    public CommentReadDto update(@Valid @RequestBody CommentUpdateDto updateDto) {
        return commentService.update(updateDto);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentReadDto> getCommentsByPostId(@PathVariable long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void remove(@PathVariable long commentId) {
        commentService.remove(commentId);
    }
}
