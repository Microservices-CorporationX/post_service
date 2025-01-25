package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static faang.school.postservice.utils.Constants.API_VERSION_1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION_1 + "/post")
public class PostController {

    private final PostService postService;
    private final PostControllerValidator postControllerValidator;

    @PutMapping("/create")
    PostResponseDto createPostDraft(@RequestBody PostRequestDto postRequestDto) {
        log.info("Create post draft: {}", postRequestDto);
        postControllerValidator.validateCreateDto(postRequestDto);
        return postService.createPostDraft(postRequestDto);
    }

    @PostMapping("/publish/{id}")
    PostResponseDto publishPostDraft(@PathVariable("id") Long postId) {
        log.info("Publish post id {}", postId);
        return postService.publishPostDraft(postId);
    }

    @PatchMapping("/update")
    PostResponseDto updatePost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Update post: {}", postRequestDto);
        postControllerValidator.validateUpdateDto(postRequestDto);
        return postService.updatePost(postRequestDto);
    }

    @DeleteMapping("/delete/{id}")
    void deletePost(@PathVariable("id") Long postId) {
        log.info("Delete post id {}", postId);
        postService.deletePost(postId);
    }

    @GetMapping("/{id}")
    PostResponseDto getPost(@PathVariable("id") Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/draftByProject/{id}")
    List<PostResponseDto> getProjectPostDrafts(@PathVariable("id") Long projectId) {
        return postService.getProjectPostDrafts(projectId);
    }

    @GetMapping("/draftByUser/{id}")
    List<PostResponseDto> getUserPostDrafts(@PathVariable("id") Long userId) {
        return postService.getUserPostDrafts(userId);
    }

    @GetMapping("/byProject/{id}")
    List<PostResponseDto> getProjectPosts(@PathVariable("id") Long projectId) {
        return postService.getProjectPosts(projectId);
    }

    @GetMapping("/byUser/{id}")
    List<PostResponseDto> getUserPosts(@PathVariable("id") Long userId) {
        return postService.getUserPosts(userId);
    }
}
