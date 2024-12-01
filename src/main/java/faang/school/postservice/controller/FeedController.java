package faang.school.postservice.controller;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.PostPublishedEvent;
import faang.school.postservice.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<RedisPostDto>> getNewsFeed(
            @PathVariable Long userId,
            @RequestHeader("x-user-id") String headerUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        if (!headerUserId.equals(String.valueOf(userId))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<RedisPostDto> posts = feedService.getNewsFeed(userId, page, pageSize);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/post")
    public ResponseEntity<Void> addPost(@RequestBody PostPublishedEvent event) {
        feedService.addPost(event);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
