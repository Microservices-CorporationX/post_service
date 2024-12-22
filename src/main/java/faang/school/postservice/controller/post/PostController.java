package faang.school.postservice.controller.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Posts")
@Tag(name = "Контроллер для управления постами")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Создание поста")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Post created",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping
    public PostDto createPost(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @Operation(summary = "Публикация поста по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post published",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/{id}")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @Operation(summary = "Обновление поста по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PatchMapping("/{id}")
    public PostDto updatePost(@PathVariable long id, @Valid @RequestBody PostDto postDto) {
        return postService.updatePost(id, postDto);
    }

    @Operation(summary = "Получение поста по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPostDto(id);
    }

    @Operation(summary = "Удаление поста по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "404", description = "Post not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @DeleteMapping("{id}")
    public PostDto deletePost(@PathVariable long id) {
        return postService.deletePost(id);
    }

    @Operation(summary = "Получение неопубликованных постов автора по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unposted posts retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Author not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping("/authors/unposted/{id}")
    public List<PostDto> getAuthorUnpostedPosts(@PathVariable("id") long authorId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(authorId, filterDto);
    }

    @Operation(summary = "Получение неопубликованных постов проекта по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unposted posts retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping("/projects/unposted/{id}")
    public List<PostDto> getProjectUnpostedPosts(@PathVariable("id") long projectId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(projectId, filterDto);
    }

    @Operation(summary = "Получение опубликованных постов автора по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posted posts retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Author not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping("/authors/posted/{id}")
    public List<PostDto> getAuthorPostedPosts(@PathVariable("id") long authorId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(authorId, filterDto);
    }

    @Operation(summary = "Получение опубликованных постов проекта по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posted posts retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping("/projects/posted/{id}")
    public List<PostDto> getProjectPostedPosts(@PathVariable("id") long projectId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(projectId, filterDto);
    }

}
