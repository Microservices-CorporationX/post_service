package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PostController implements PostControllerOas {
    private final PostService postService;

    @PostMapping("/posts")
    public PostDto createPost(@RequestBody PostDto post) {
        if (post.getAuthorId() == null && post.getProjectId() == null) {
            throw new DataValidationException("Post must be have user or project");
        }
        return postService.create(post);
    }

    @PutMapping("/posts/{postId}/publish")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publish(postId);
    }

    @PutMapping("/posts/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestBody PostDto postDto) {
        if (postId != postDto.getId()) {
            throw new DataValidationException("Path postId and body postId are different");
        }
        return postService.update(postDto);
    }

    @DeleteMapping("/posts/{postId}")
    public PostDto deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/posts/author/{authorId}/published")
    public List<PostDto> getPublishedPostByAuthorId(@PathVariable long authorId) {
        return postService.getAllPublishedByAuthorId(authorId);
    }

    @GetMapping("/posts/author/{authorId}/unpublished")
    public List<PostDto> getNonPublishedPostByAuthorId(@PathVariable long authorId) {
        return postService.getAllNonPublishedByAuthorId(authorId);
    }

    @GetMapping("/posts/project/{projectId}/published")
    public List<PostDto> getPublishedPostByProjectId(@PathVariable long projectId) {
        return postService.getAllPublishedByProjectId(projectId);
    }

    @GetMapping("/posts/project/{projectId}/unpublished")
    public List<PostDto> getNonPublishedPostByProjectId(@PathVariable long projectId) {
        return postService.getAllNonPublishedByProjectId(projectId);
    }

}
