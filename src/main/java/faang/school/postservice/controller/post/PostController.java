package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${post.domain.path}/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<PostResponseDto> getPostsByHashtag(@RequestParam("hashtag") String hashtag) {
        return postService.getPostsByHashtag(hashtag);
    }
}
