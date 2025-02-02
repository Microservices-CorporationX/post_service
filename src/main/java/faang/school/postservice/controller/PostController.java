package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts")
@Tag(name = "Посты")
public class PostController {
    private final PostService postService;

    @PostMapping
    @Operation(summary = "Создание черновика поста, может создать либо проект, либо пользователь")
    public PostReadDto createPostDraft(@RequestBody PostCreateDto dto) {
        return postService.createPostDraft(dto);
    }

    @PatchMapping("/{postId}/publishing")
    @Operation(summary = "Публикация поста")
    public PostReadDto publishPost(
            @PathVariable
            @Parameter(description = "Идентификатор поста", required = true)
            long postId
    ) {
        return postService.publishPost(postId);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Удаление поста")
    public PostReadDto softDeletePost(
            @PathVariable
            @Parameter(description = "Идентификатор поста", required = true)
            long postId
    ) {
        return postService.softDeletePost(postId);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "Обновление поста")
    public PostReadDto updatePost(
            @RequestBody
            PostUpdateDto dto,
            @PathVariable
            @Parameter(description = "Идентификатор поста", required = true)
            long postId
    ) {
        return postService.updatePost(postId, dto);
    }

    @GetMapping("/{id}/drafts")
    @Operation(summary = "Получение черновиков постов автора")
    public List<PostReadDto> getPostsDrafts(
            @PathVariable
            @Parameter(description = "Идентификатор автора", required = true)
            long id,
            @RequestParam
            @Parameter(description = "Тип владельца поста", required = true)
            @Schema(allowableValues = {"author", "post"})
            String type
    ) {
        return postService.getAllDrafts(id, PostOwnerType.fromString(type));
    }

    @GetMapping("/{id}/published")
    @Operation(summary = "Получение опубликованных постов автора")
    public List<PostReadDto> getPublishedPosts(
            @PathVariable
            @Parameter(description = "Идентификатор автора", required = true)
            long id,
            @RequestParam
            @Parameter(description = "Тип владельца поста", required = true)
            @Schema(allowableValues = {"author", "post"})
            String type
    ) {
        return postService.getAllPublished(id, PostOwnerType.fromString(type));
    }
}
