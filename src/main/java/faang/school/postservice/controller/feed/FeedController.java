package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.FeedHeater;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {
    private final UserContext userContext;
    private final FeedService feedService;
    private final FeedHeater feedHeater;

    @GetMapping
    public List<PostResponseDto> getUserFeed(@RequestParam(required = false) long postId) {
       return feedService.getUserFeed(postId, userContext.getUserId());
    }

    @GetMapping("/heat")
    public void cacheHeat() {
        feedHeater.start();
    }
}
