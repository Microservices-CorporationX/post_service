package ru.corporationx.postservice.controller.like;

import ru.corporationx.postservice.dto.like.LikeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "like-controller", description = "Likes posts and comments (like comment\n" +
        "like post\n" +
        "Remove like under comment\n" +
        "Remove like under post)")
public interface LikeControllerOas {

    @Operation(summary = "Лайк комментария", description = "Необходимо передать ID комментария и JSON лайка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "комментария не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    LikeDto likeComment(Long commentId, LikeDto likeDto);

    @Operation(summary = "Лайк поста", description = "Необходимо передать ID поста и JSON лайка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    LikeDto likePost(Long postId, LikeDto likeDto);

    @Operation(summary = "Удаление лайка под комментариев", description = "Необходимо передать ID комментария и JSON лайка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "комментария не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    LikeDto removeLikeUnderComment(long commentId, LikeDto likeDto);

    @Operation(summary = "Удаление лайка под постом", description = "Необходимо передать ID поста и JSON лайка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    LikeDto removeLikeUnderPost(long postId, LikeDto likeDto);
}
