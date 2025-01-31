package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.SavePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PostController {

    private final PostValidator postValidator;
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public PostDto create(@Valid @NotNull @RequestBody SavePostDto savePostDto) {
        Post post = postMapper.toEntity(savePostDto);
        postValidator.validatePostAuthorExist(post);
        return postService.create(savePostDto);
    }

    @PutMapping("/{id}")
    public PostDto update(@PathVariable long id, @NotNull @RequestBody SavePostDto postSaveDto) {
        Post post = postMapper.toEntity(postSaveDto);
        postValidator.validatePostAuthorExist(post);
        return postService.update(id, postSaveDto);
    }

    @DeleteMapping("/{id}")
    public PostDto delete(@PathVariable long id) {
        return postService.delete(id);
    }

    @PostMapping("/{id}/publish")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publish(id);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @GetMapping("/users/{authorId}/drafts")
    public List<PostDto> getDraftPostsByAuthorId(@PathVariable long authorId) {
        return postService.getDraftPostsByAuthorId(authorId);
    }

    @GetMapping("/users/{authorId}/published")
    public List<PostDto> getPublishedPostsByAuthorId(@PathVariable long authorId) {
        return postService.getPublishedPostsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/drafts")
    public List<PostDto> getDraftPostsByProjectId(@PathVariable long projectId) {
        return postService.getDraftPostsByProjectId(projectId);
    }

    @GetMapping( "/projects/{projectId}/published")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable long projectId) {
        return postService.getPublishedPostsByProjectId(projectId);
    }
}