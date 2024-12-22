package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post/comment")
@RequiredArgsConstructor
@Tag(name = "Контроллер для управления комментариями")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Создание комментария")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Comment created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @Operation(summary = "Обновление комментария")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping
    public CommentDto updateComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @Operation(summary = "Получение комментария по id")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Comment retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @Operation(summary = "Удаление комментария по id")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Comment deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))}),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
