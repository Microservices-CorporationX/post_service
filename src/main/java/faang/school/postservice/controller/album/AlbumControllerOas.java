package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "album-controller", description = "Post albums (create album\n" +
        "add post\n" +
        "delete post\n" +
        "add album to favorites\n" +
        "remove album from favorites\n" +
        "get album by ID\n" +
        "get all albums\n" +
        "get favorite albums\n" +
        "update album\n" +
        "delete album)")
public interface AlbumControllerOas {

    @Operation(summary = "Создание альбома", description = "Необходимо передать заголовок и описание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "автора не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    AlbumDto create(AlbumDto albumDto);

    @Operation(summary = "Добавление поста в альбом", description = "Необходимо передать ID альбома и ID поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома или поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    AlbumDto addPost(Long albumId, Long postId);

    @Operation(summary = "Удаление поста из альбома", description = "Необходимо передать ID альбома и ID поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома или поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    AlbumDto deletePost(Long albumId, Long postId);

    @Operation(summary = "Добавление альбома в избранные", description = "Необходимо передать ID альбома и ID пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома или пользователя не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    void addAlbumToFavorites(Long albumId, Long userId);

    @Operation(summary = "Удаление альбома из избранных", description = "Необходимо передать ID альбома и ID пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома или пользователя не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    void removeAlbumFromFavorites(Long albumId, Long userId);

    @Operation(summary = "Получение альбома по ID", description = "Необходимо передать ID альбома")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    AlbumDto get(Long albumId);

    @Operation(summary = "Получение всех альбомов пользователя",
            description = "Необходимо передать ID пользователя. Опционально: фильтр (по заголовку/ по дате создания)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбомов не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<AlbumDto> getAll(Long userId, AlbumFilterDto filter);

    @Operation(summary = "Получение всех избранных альбомов пользователя",
            description = "Необходимо передать ID пользователя. Опционально: фильтр (по заголовку/ по дате создания)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбомов не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<AlbumDto> getFavorites(Long userId, AlbumFilterDto filter);

    @Operation(summary = "Обновление альбома", description = "Необходимо передать заголовок и описание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    AlbumDto update(AlbumDto albumDto);

    @Operation(summary = "Удаление альбома", description = "Необходимо передать ID альбома и ID пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "альбома или пользователя не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    void delete(Long albumId, Long userId);
}
