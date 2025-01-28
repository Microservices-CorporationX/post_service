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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentReadDto create(@Valid @RequestBody CommentCreateDto createDto) {
        return commentService.create(createDto);
    }

    @PutMapping
    public CommentReadDto update(@Valid @RequestBody CommentUpdateDto updateDto) {
        return commentService.update(updateDto);
    }

    @GetMapping
    public List<CommentReadDto> getCommentsByPostId(@RequestParam(required = false) long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public void remove(@PathVariable long commentId) {
        commentService.remove(commentId);
    }
}
