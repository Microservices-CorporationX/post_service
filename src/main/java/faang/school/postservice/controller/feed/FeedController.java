package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/feeds")
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping("/")
    public List<PostFeedDto> getFeed(@RequestParam Long postId){
        long userId = userContext.getUserId();
        return feedService.getPosts(postId, userId);
    }
}
