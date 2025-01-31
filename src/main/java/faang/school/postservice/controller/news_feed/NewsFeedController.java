package faang.school.postservice.controller.news_feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.cache.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news_feed")
@RequiredArgsConstructor
public class NewsFeedController {
    private final NewsFeedService newsFeedService;
    private final UserContext userContext;

    @GetMapping
    public List<PostResponseDto> getFeed(@RequestParam(required = false) Long lastViewedPostId) {
        Long userId = userContext.getUserId();
        return newsFeedService.getFeed(userId, lastViewedPostId);
    }
}