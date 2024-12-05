package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostControllerValidator;
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
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostControllerValidator validator;
    private final PostService postService;

    @PostMapping
    public PostUpdateDto createDraftPost(@RequestBody @Valid PostCreateDto postCreateDto) {
        validator.validatePostCreators(postCreateDto.authorId(), postCreateDto.projectId());
        return postService.createDraft(postCreateDto);
    }

    @PostMapping("/{id}/public")
    public PostUpdateDto publicPost(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.publicPost(id);
    }

    @PutMapping
    public PostUpdateDto updatePost(@RequestBody @Valid PostUpdateDto postUpdateDto) {
        validator.validatePostCreators(postUpdateDto.authorId(), postUpdateDto.projectId());
        return postService.updatePost(postUpdateDto);
    }

    @DeleteMapping("/{id}")
    public PostUpdateDto softDeletePost(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.softDeletePost(id);
    }

    @GetMapping("/{id}")
    public PostUpdateDto getPostById(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPostById(id);
    }

    @GetMapping("/author/{id}/drafts")
    public List<PostUpdateDto> getPostDraftsByAuthorId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPostDraftsByAuthorId(id);

    }

    @GetMapping("/project/{id}/drafts")
    public List<PostUpdateDto> getPostDraftsByProjectId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPostDraftsByProjectId(id);
    }

    @GetMapping("/author/{id}/published")
    public List<PostUpdateDto> getPublishedPostsByAuthorId(
            @PathVariable
            @NotNull(message = "id is null")
            @Min(value = 1, message = "id не должен быть меньше 1")
            long id
    ) {
        return postService.getPublishedPostsByAuthorId(id);
    }

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
