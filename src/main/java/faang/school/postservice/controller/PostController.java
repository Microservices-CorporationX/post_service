package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    public PostReadDto addHashtagToPost(long postId, long hashtagId) {
        //TODO дописать метод в Cервисе
        return null;
    }

    public void removeHashtagFromPost(long postId, long hashtagId) {
        //TODO дописать метод в Cервисе
    }

    public List<PostReadDto> getPostsByHashtagId(long hashtagId) {
        //TODO дописать метод в Cервисе
        return null;
    }
}
