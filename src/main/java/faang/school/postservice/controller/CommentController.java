package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.utilities.UrlUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.COMMENT)
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @PutMapping()
    public CommentDto updateComment(@RequestBody @Valid CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping(UrlUtils.ID)
    public List<CommentDto> getAllComments(@PathVariable("id") @Min(1) Long id) {
        return commentService.getAllComments(id);
    }

    @DeleteMapping(UrlUtils.ID)
    public void deleteComment(@PathVariable("id") @NotNull @Min(1) Long commentId) {
        commentService.deleteComment(commentId);
    }
}
