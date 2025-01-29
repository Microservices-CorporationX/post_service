package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<FeedPostDto> getFeed(@PathParam("postId") Long postId) {
        return feedService.getFeed(postId);
    }
}
