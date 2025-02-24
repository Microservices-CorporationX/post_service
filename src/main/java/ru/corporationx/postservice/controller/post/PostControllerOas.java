package ru.corporationx.postservice.controller.post;

import ru.corporationx.postservice.dto.post.PostAuthorFilterDto;
import ru.corporationx.postservice.dto.post.PostDto;
import ru.corporationx.postservice.dto.resource.ResourceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "post-controller", description = "Posts (create post\n" +
        "publish post\n" +
        "update post\n" +
        "delete post\n" +
        "get published post by author ID\n" +
        "get non published post by author ID\n" +
        "get published post by project ID\n" +
        "get non published post by project ID)")
public interface PostControllerOas {

    @Operation(summary = "Создание поста", description = "Необходимо передать JSON поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "автора не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    PostDto createPost(PostDto post);

    @Operation(summary = "Публикация поста", description = "Необходимо передать ID поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    PostDto publishPost(long postId);

    @Operation(summary = "Обновление поста", description = "Необходимо передать ID поста и JSON поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "автора или поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    PostDto updatePost(long postId, PostDto postDto);

    @Operation(summary = "Удаление поста", description = "Необходимо передать ID поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    PostDto deletePost(long postId);

    @Operation(summary = "Получение всех опубликованных постов от одного автора", description = "Необходимо передать ID пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "автора не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<PostDto> getPublishedPostByAuthorId(long authorId);

    @Operation(summary = "Получение всех неопубликованных постов от одного автора", description = "Необходимо передать ID пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "автора не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<PostDto> getNonPublishedPostByAuthorId(long authorId);

    @Operation(summary = "Получение всех опубликованных постов под проектом", description = "Необходимо передать ID проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "проекта не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<PostDto> getPublishedPostByProjectId(long projectId);

    @Operation(summary = "Получение всех неопубликованных постов под проектом", description = "Необходимо передать ID проекта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "проекта не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<PostDto> getNonPublishedPostByProjectId(long projectId);

    @Operation(summary = "Получение поста по фильтрам", description = "Необходимо передать JSON с фильтрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<PostDto> getPostBy(PostAuthorFilterDto filter);

    @Operation(summary = "Добавление изображения к посту", description = "Необходимо передать ID поста и файл изображения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "успешное создание запроса"),
            @ApiResponse(responseCode = "400", description = "неправильный запрос",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "поста не существует",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    List<ResourceDto> addPictures(long postId, MultipartFile[] files);
}
