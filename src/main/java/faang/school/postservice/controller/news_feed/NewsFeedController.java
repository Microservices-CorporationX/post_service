package faang.school.postservice.controller.news_feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.news_feed_models.NewsFeedPost;
import faang.school.postservice.service.news_feed_service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news_feed")
@RequiredArgsConstructor
public class NewsFeedController {
    private final NewsFeedService newsFeedService;
    private final UserContext userContext;

    @GetMapping
    public List<NewsFeedPost> getFeed(@RequestParam(required = false) Long lastViewedPostId) {
        Long userId = userContext.getUserId();
        return newsFeedService.getFeed(userId, lastViewedPostId);
    }

    @PostMapping("/heat")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void heatFeed(){
        newsFeedService.heatFeed();
    }
}