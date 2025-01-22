package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${post-service.api-version}/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "create-draft")
    PostDto createDraft(@RequestBody PostDto postDto){
        return postService.createDraft(postDto);
    }
}
