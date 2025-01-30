package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.NewsFeedService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/like")
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;
    private final NewsFeedService newsFeedService;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikePostDto likePost(@PathVariable @Positive long postId) {
        @Positive long userId = userContext.getUserId();
        LikePostDto likePostDto = likeService.createLikePost(postId, userId);
        newsFeedService.sendLikeEventAsync(likePostDto);
        return likePostDto;
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeCommentDto likeComment(@PathVariable @Positive long commentId) {
        @Positive long userId = userContext.getUserId();
        return likeService.createLikeComment(commentId, userId);
    }

    @DeleteMapping("/post/{postId}")
    public void removeLikeFromPost(@PathVariable @Positive long postId) {
        @Positive long userId = userContext.getUserId();
        likeService.deleteLikeFromPost(postId, userId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void removeLikeFromComment(@PathVariable @Positive long commentId) {
        @Positive long userId = userContext.getUserId();
        likeService.deleteLikeFromComment(commentId, userId);
    }
}