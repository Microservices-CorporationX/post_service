package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/post")
@Tag(name = "Post Controller", description = "API для работы с постами")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostControllerValidator validator;
    private final PostService postService;

    @Operation(summary = "создать драфт пост")
    @PostMapping
    public PostUpdateDto createDraftPost(@RequestBody @Parameter(description = "данные для создания драфт поста") @Valid PostCreateDto postCreateDto) {
        validator.validatePostCreators(postCreateDto.authorId(), postCreateDto.projectId());
        return postService.createDraft(postCreateDto);
    }

    @Operation(summary = "опубликовать пост")
    @PostMapping("/{id}/public")
    public PostUpdateDto publicPost(
            @PathVariable
            @Parameter(description = "данные для создания опубликования поста")
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.publicPost(id);
    }

    @Operation(summary = "обновить пост")
    @PutMapping
    public PostUpdateDto updatePost(@RequestBody @Parameter(description = "данные для обновления поста") @Valid PostUpdateDto postUpdateDto) {
        validator.validatePostCreators(postUpdateDto.authorId(), postUpdateDto.projectId());
        return postService.updatePost(postUpdateDto);
    }

    @Operation(summary = "удалить пост")
    @DeleteMapping("/{id}")
    public PostUpdateDto softDeletePost(
            @PathVariable
            @Parameter(description = "данные для удаления поста")
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.softDeletePost(id);
    }

    @Operation(summary = "получить пост по ID", description = "Возвращает пост с указанным ID")
    @ApiResponse(responseCode = "200", description = "Пост найден")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @GetMapping("/{id}")
    public PostUpdateDto getPostById(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPostById(id);
    }

    @Operation(summary = "получить draft пост по ID автора", description = "Возвращает пост с указанным ID автора")
    @ApiResponse(responseCode = "200", description = "Пост найден")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @GetMapping("/author/{id}/drafts")
    public List<PostUpdateDto> getPostDraftsByAuthorId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPostDraftsByAuthorId(id);

    }

    @Operation(summary = "получить пост по ID проэкта", description = "Возвращает пост с указанным ID проэкта")
    @ApiResponse(responseCode = "200", description = "Пост найден")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @GetMapping("/project/{id}/drafts")
    public List<PostUpdateDto> getPostDraftsByProjectId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPostDraftsByProjectId(id);
    }

    @Operation(summary = "получить опубликованные посты по ID автора", description = "Возвращает опубликованные посты с указанным ID автора")
    @ApiResponse(responseCode = "200", description = "Пост найден")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @GetMapping("/author/{id}/published")
    public List<PostUpdateDto> getPublishedPostsByAuthorId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPublishedPostsByAuthorId(id);
    }

    @Operation(summary = "получить опубликованные посты по ID проэкта", description = "Возвращает опубликованные посты с указанным ID проэкта")
    @ApiResponse(responseCode = "200", description = "Пост найден")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @GetMapping("/project/{id}/published")
    public List<PostUpdateDto> getPublishedPostsByProjectId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPublishedPostsByProjectId(id);
    }
}
