package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostUpdateDto;
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
@RequestMapping("/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostReadDto createPostDraft(@RequestBody PostCreateDto dto) {
        return postService.createPostDraft(dto);
    }

    @PatchMapping("/{postId}/publishing")
    public PostReadDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @DeleteMapping("/{postId}")
    public PostReadDto softDeletePost(@PathVariable long postId) {
        return postService.softDeletePost(postId);
    }

    @PatchMapping("/{postId}")
    public PostReadDto updatePost(@RequestBody PostUpdateDto dto, @PathVariable long postId) {
        return postService.updatePost(postId, dto);
    }

    @GetMapping("/{id}/drafts")
    public List<PostReadDto> getPostsDrafts(@PathVariable long id, @RequestParam String type) {
        return postService.getAllDrafts(id, PostOwnerType.fromString(type));
    }

    @GetMapping("/{id}/published")
    public List<PostReadDto> getPublishedPosts(@PathVariable long id, @RequestParam String type) {
        return postService.getAllPublished(id, PostOwnerType.fromString(type));
    }

    public List<PostReadDto> getPostsByHashtagId(long hashtagId) {
        //TODO дописать метод в Cервисе
        return null;
    }
}
