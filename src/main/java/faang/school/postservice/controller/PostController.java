package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
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
@RequestMapping("/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto createPostDraft(@RequestBody PostCreateDto dto) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null) {
            throw new DataValidationException("Пост может создать либо автор, либо проект.");
        }
        return postService.createPostDraft(dto);
    }

    @PatchMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @DeleteMapping("/{postId}")
    public PostDto softDeletePost(@PathVariable long postId) {
        return postService.softDeletePost(postId);
    }

    @PatchMapping("/{postId}")
    public PostDto updatePost(@RequestBody PostUpdateDto dto, @PathVariable long postId) {
        if (dto.getContent() != null && dto.getContent().isBlank()) {
            throw new DataValidationException("Content не может быть пустым");
        }
        return postService.updatePost(postId, dto);
    }

    @GetMapping("/{id}/drafts")
    public List<PostDto> getPostsDrafts(@PathVariable long id, @RequestParam String type) {
        if (type.equalsIgnoreCase("project")) {
            return postService.getAllDraftsByProject(id);
        }
        if (type.equalsIgnoreCase("author")) {
            return postService.getAllDraftsByAuthor(id);
        }
        throw new DataValidationException("Неверный тип владельца поста.");
    }

    @GetMapping("/{id}/published")
    public List<PostDto> getPublishedPosts(@PathVariable long id, @RequestParam String type) {
        if (type.equalsIgnoreCase("project")) {
            return postService.getAllPublishedPostsByProject(id);
        }
        if (type.equalsIgnoreCase("author")) {
            return postService.getAllPublishedPostsByAuthor(id);
        }
        throw new DataValidationException("Неверный тип владельца поста.");
    }
}
