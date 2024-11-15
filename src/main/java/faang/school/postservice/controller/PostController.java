package faang.school.postservice.controller;

import faang.school.postservice.docs.post.CreatePostDoc;
import faang.school.postservice.docs.post.DeletePostDoc;
import faang.school.postservice.docs.post.GetDraftPostByAuthorDoc;
import faang.school.postservice.docs.post.GetDraftPostByProjectDoc;
import faang.school.postservice.docs.post.GetPostDoc;
import faang.school.postservice.docs.post.GetPublishedPostByAuthorDoc;
import faang.school.postservice.docs.post.GetPublishedPostByProjectDoc;
import faang.school.postservice.docs.post.PublishPostDoc;
import faang.school.postservice.docs.post.UpdatePostDoc;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    @CreatePostDoc
    public ResponseEntity<ResponsePostDto> create(@Valid @RequestBody CreatePostDto createPostDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(createPostDto));
    }

    @PutMapping("{postId}/publish")
    @PublishPostDoc
    public ResponseEntity<ResponsePostDto> publish(
            @Valid
            @PathVariable
            @Positive(message = "Post id must be positive")
            @NotNull(message = "Post id cannot be null")
            Long postId
    ) {
        return ResponseEntity.ok(postService.publish(postId));
    }

    @PutMapping("/{postId}")
    @UpdatePostDoc
    public ResponseEntity<ResponsePostDto> update(
            @Valid
            @PathVariable
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive")
            Long postId,
            @Valid @RequestBody UpdatePostDto updatePostDto
    ) {
        return ResponseEntity.ok(postService.update(postId, updatePostDto));
    }

    @DeleteMapping("/{postId}")
    @DeletePostDoc
    public ResponseEntity<Void> delete(
            @PathVariable
            @Valid
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long postId
    ) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    @GetPostDoc
    public ResponseEntity<ResponsePostDto> getPostById(
            @PathVariable
            @Valid
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long postId
    ) {
        return ResponseEntity.ok(postService.getById(postId));
    }

    @GetMapping("/draft/author/{userId}")
    @GetDraftPostByAuthorDoc
    public ResponseEntity<List<ResponsePostDto>> getDraftByUserId(
            @PathVariable
            @Valid
            @NotNull(message = "User id cannot be null")
            @Positive(message = "User id must be positive") Long userId
    ) {
        return ResponseEntity.ok(postService.getDraftsByUserId(userId));
    }

    @GetMapping("/draft/project/{projectId}")
    @GetDraftPostByProjectDoc
    public ResponseEntity<List<ResponsePostDto>> getDraftByProjectId(
            @PathVariable
            @Valid
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long projectId
    ) {
        return ResponseEntity.ok(postService.getDraftsByProjectId(projectId));
    }

    @GetMapping("/published/author/{userId}")
    @GetPublishedPostByAuthorDoc
    public ResponseEntity<List<ResponsePostDto>> getPublishedByUserId(
            @Valid
            @PathVariable
            @NotNull(message = "User id cannot be null")
            @Positive(message = "User id must be positive")
            Long userId
    ) {
        return ResponseEntity.ok(postService.getPublishedByUserId(userId));
    }

    @GetMapping("/published/project/{projectId}")
    @GetPublishedPostByProjectDoc
    public ResponseEntity<List<ResponsePostDto>> getPublishedByProjectId(
            @Valid
            @PathVariable
            @NotNull(message = "Project id cannot be null")
            @Positive(message = "Project id must be positive")
            Long projectId,
            @RequestParam
            @NotNull(message = "Page number cannot be null")
            @NotBlank(message = "Page number cannot be blank")
            Long authorId
    ) {
        return ResponseEntity.ok(postService.getPublishedByProjectId(projectId, authorId));
    }
}
