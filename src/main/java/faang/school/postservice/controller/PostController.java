package faang.school.postservice.controller;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public PostResultResponse createPost(@RequestBody PostCreatingRequest postCreatingDto) {
        return postService.createPost(postCreatingDto);
    }

    @PatchMapping("/{postId}/publish")
    public PostResultResponse publishPost(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @PatchMapping("/{postId}/update")
    public PostResultResponse updatePost(@PathVariable Long postId, @RequestBody String updatingContent) {
        return postService.updatePost(postId, updatingContent);
    }

    @DeleteMapping("/{postId}")
    public PostResultResponse softDelete(@PathVariable Long postId) {
        return postService.softDelete(postId);
    }

    @GetMapping("/author/{authorId}/no-published")
    public List<PostResultResponse> getNoPublishedPostsByAuthor(@PathVariable Long authorId) {
        return postService.getNoPublishedPostsByAuthor(authorId);
    }

    @GetMapping("/project/{projectId}/no-published")
    public List<PostResultResponse> getNoPublishedPostsByProject(@PathVariable Long projectId) {
        return postService.getNoPublishedPostsByProject(projectId);
    }

    @GetMapping("/author/{authorId}/published")
    public List<PostResultResponse> getPublishedPostsByAuthor(@PathVariable Long authorId) {
        return postService.getPublishedPostsByAuthor(authorId);
    }

    @GetMapping("/project/{projectId}/published")
    public List<PostResultResponse> getPublishedPostsByProject(@PathVariable Long projectId) {
        return postService.getPublishedPostsByProject(projectId);
    }
}
