package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "comment-controller", description = "Comments (create comment\n" +
        "update comment\n" +
        "get comments\n" +
        "delete comment)")
public interface CommentControllerOas {

    @Operation(summary = "Создание комментария", description = "Необходимо передать ID поста и JSON комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    CommentDto createComment(long postId, CommentDto comment);

    @Operation(summary = "Обновление комментария", description = "Необходимо передать ID комментария и JSON комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "комментария или поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    CommentDto updateComment(long commentId, CommentDto comment);

    @Operation(summary = "Получение всех комментариев под постом", description = "Необходимо передать ID поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<CommentDto> getComments(long postId);

    @Operation(summary = "Удаление комментария", description = "Необходимо передать ID комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "комментария не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    void deleteComment(long commentId);
}
