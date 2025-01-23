package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static faang.school.postservice.constant.PostErrorMessages.POST_CANNOT_BE_NULL;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${post-service.api-version}/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create-draft")
    public PostDto createDraft(@RequestBody PostDto postDto) {
        log.info("iim here");
        return postService.createDraft(postDto);
    }

    @PutMapping(value = "/publish/{postId}")
    public PostDto publish(@PathVariable @NotNull(message = POST_CANNOT_BE_NULL) Long postId) {
        return postService.publish(postId);
    }

    @PatchMapping(value = "/update")
    public PostDto update(@RequestParam Long id, @RequestParam String content) {
        return postService.update(id, content);
    }

    @PatchMapping(value = "/soft-delete/{id}")
    public PostDto softDelete(@PathVariable Long id) {
        return postService.softDelete(id);
    }

    @GetMapping(value = "/{id}")
    public PostDto getById(@PathVariable Long id) {
        return postService.getById(id);
    }
}
