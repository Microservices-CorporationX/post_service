package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController implements PostControllerOas {
    private final PostService postService;

    @PostMapping
    public PostDto createPost(@RequestBody PostDto post) {
        if (post.getAuthorId() == null && post.getProjectId() == null) {
            throw new DataValidationException("Post must be have user or project");
        }
        return postService.create(post);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publish(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestBody PostDto postDto) {
        if (postId != postDto.getId()) {
            throw new DataValidationException("Path postId and body postId are different");
        }
        return postService.update(postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/author/{authorId}/published")
    public List<PostDto> getPublishedPostByAuthorId(@PathVariable long authorId) {
        return postService.getAllPublishedByAuthorId(authorId);
    }

    @GetMapping("/author/{authorId}/unpublished")
    public List<PostDto> getNonPublishedPostByAuthorId(@PathVariable long authorId) {
        return postService.getAllNonPublishedByAuthorId(authorId);
    }

    @GetMapping("/project/{projectId}/published")
    public List<PostDto> getPublishedPostByProjectId(@PathVariable long projectId) {
        return postService.getAllPublishedByProjectId(projectId);
    @GetMapping
    public List<PostDto> getPostBy(@ModelAttribute @Valid PostAuthorFilterDto filter) {
        return postService.getPostsBy(filter);
    }

    @GetMapping("/project/{projectId}/unpublished")
    public List<PostDto> getNonPublishedPostByProjectId(@PathVariable long projectId) {
        return postService.getAllNonPublishedByProjectId(projectId);
    @PutMapping("/{postId}/pictures")
    public List<ResourceDto> addPictures(@PathVariable long postId, @RequestParam MultipartFile[] files) {
        return postService.addPictures(postId, files);
    }

}
