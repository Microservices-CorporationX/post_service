package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static faang.school.postservice.constant.PostErrorMessages.POST_CANNOT_BE_NULL;

@RestController
@RequiredArgsConstructor
@RequestMapping("${post-service.api-version}/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create-draft")
    PostDto createDraft(@RequestBody PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PutMapping(value = "/publish/{postId}")
    PostDto publish(@PathVariable @NotNull(message = POST_CANNOT_BE_NULL) Long postId) {
        return postService.publish(postId);
    }

    @PatchMapping(value = "/update")
    PostDto update(@RequestBody PostDto postDto) {
        return postService.update(postDto);
    }

}
