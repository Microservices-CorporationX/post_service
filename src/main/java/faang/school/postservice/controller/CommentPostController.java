package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentPostServiceImpl;
import faang.school.postservice.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentPostController {

    private final CommentPostServiceImpl service;
    private final UserContext userContext;

    @GetMapping("/post/{postId}")
    public List<CommentDto> getAllCommentsByPostId(@PathVariable(value = "postId") Long postId) {
        var comments = service.findCommentsByPostId(postId);
        return MapperUtil.convertList(comments, CommentDto.class);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable(value = "commentId") Long commentId) {
        Comment comment = service.getById(commentId);
        return MapperUtil.convertClass(comment, CommentDto.class);
    }

    @PostMapping("/{postId}")
    public CommentDto createComment(@PathVariable(value = "postId") Long postId,
                                    @Valid @RequestBody CommentDto commentDto) {
        var authorId = userContext.getUserId();
        Comment entity = MapperUtil.convertClass(commentDto, Comment.class);
        entity.setAuthorId(authorId);
        var comment = service.create(postId, entity);
        return MapperUtil.convertClass(comment, CommentDto.class);
    }

    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable(value = "commentId") Long commentId,
                                    @Valid @RequestBody CommentDto commentDto) {
        var authorId = userContext.getUserId();
        Comment entity = MapperUtil.convertClass(commentDto, Comment.class);
        entity.setAuthorId(authorId);
        var comment = service.update(commentId, entity);
        return MapperUtil.convertClass(comment, CommentDto.class);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable(value = "commentId") Long commentId) {
        service.delete(commentId);
    }
}
