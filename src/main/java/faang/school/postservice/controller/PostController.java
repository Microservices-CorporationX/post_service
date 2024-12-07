package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post_file.PostFileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostFileService postFileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@Valid @RequestBody PostRequestDto postRequestDtoDto) {
        return postService.createPost(postRequestDtoDto);
    }

    @PutMapping("/{postId}")
    public PostDto publishPost(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping()
    public PostDto updatePost(@Valid @RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PatchMapping("/{postId}/disable")
    public void disablePostById(@PathVariable Long postId) {
        postService.disablePostById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/drafts/users/{userId}")
    public List<PostDto> getAllNoPublishPostsByUserId(@PathVariable Long userId) {
        return postService.getAllNoPublishPostsByUserId(userId);
    }

    @GetMapping("/drafts/projects/{projectId}")
    public List<PostDto> getAllNoPublishPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllNoPublishPostsByProjectId(projectId);
    }

    @GetMapping("/users/{userId}")
    public List<PostDto> getAllPostsByUserId(@PathVariable Long userId) {
        return postService.getAllPostsByUserId(userId);
    }

    @GetMapping("/projects/{projectId}")
    public List<PostDto> getAllPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }

    @PostMapping("/{postId}/files")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFilesToPost(
            @RequestPart @NotNull @NotEmpty List<@NotNull MultipartFile> files,
            @PathVariable @Min(1) long postId
    ) {
        postFileService.uploadFiles(files, postId);
        return "Upload started";
    }
}