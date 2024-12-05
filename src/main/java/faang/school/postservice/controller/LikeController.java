package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{postId}/likes")
    public ResponseEntity<List<UserDto>> getUsersWhoLikePostByPostId(@PathVariable @Positive long postId) {
        log.info("Request for users by post id: {}", postId);
        return ResponseEntity.ok(likeService.getUsersWhoLikePostByPostId(postId));
    }

    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<List<UserDto>> getUsersWhoLikeComments(@PathVariable @Positive long commentId) {
        log.info("Request for users by comment id: {}", commentId);
        return ResponseEntity.ok(likeService.getUsersWhoLikeComments(commentId));
    }

    @PostMapping("/post/like")
    public ResponseEntity<ResponseLikeDto> addLikeToPost(@Valid @RequestBody LikeDto likeDto) {
        log.info("Request for add like to post: {} by user: {}", likeDto.getPostId(), likeDto.getUserId());
        ResponseLikeDto responseLikeDto = likeService.addLikeToPost(likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseLikeDto);
    }
}
