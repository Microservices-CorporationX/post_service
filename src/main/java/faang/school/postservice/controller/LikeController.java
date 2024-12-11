package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{postId}/likes")
    public List<UserDto> getUsersWhoLikePostByPostId(@PathVariable @Positive long postId) {
        log.info("Request for users by post id: {}", postId);
        return likeService.getUsersWhoLikePostByPostId(postId);
    }

    @GetMapping("/comment/{commentId}/likes")
    public List<UserDto> getUsersWhoLikeComments(@PathVariable @Positive long commentId) {
        log.info("Request for users by comment id: {}", commentId);
        return likeService.getUsersWhoLikeComments(commentId);
    }
}
