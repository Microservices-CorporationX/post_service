package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.FeedService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public List<PostDto> getUserFeed(@Nullable @RequestParam("postId") Long postId) {
        long userId = userContext.getUserId();
        return feedService.getFeedByUserId(postId, userId);
    }

    @GetMapping("/heat")
    public void sendHeatEventsAsync() {
        feedService.sendHeatEvents();
    }
}
