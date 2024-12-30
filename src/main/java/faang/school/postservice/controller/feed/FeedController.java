package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedCacheService feedCacheService;
    private final FeedHeatService feedHeatService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public List<PostDto> getUserFeed(@RequestParam("postId") Long postId) {
        Long userId = userContext.getUserId();

//        if (userFeed.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }

        return feedCacheService.getFeedByUserId(postId, userId);
    }

    @GetMapping("/heat")
    public void sendHeatEventsAsync() {
        feedHeatService.sendHeatEvents();
    }
}