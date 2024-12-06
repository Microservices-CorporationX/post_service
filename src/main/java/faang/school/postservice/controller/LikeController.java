package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Like", description = "The Like activity")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<UserDto>> getUsersWhoLikePostByPostId(
            @PathVariable
            @Positive(message = "Post id must be positive")
            Long postId) {
        log.info("Request for users by post id: {}", postId);
        return ResponseEntity.ok(likeService.getUsersWhoLikePostByPostId(postId));
    }

    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<List<UserDto>> getUsersWhoLikeComments(
            @PathVariable
            @Positive(message = "Comment id must be positive")
            Long commentId
    ) {
        log.info("Request for users by comment id: {}", commentId);
        return ResponseEntity.ok(likeService.getUsersWhoLikeComments(commentId));
    }

    @PostMapping("/posts/like")
    public ResponseEntity<ResponseLikeDto> addLikeToPost(@Valid @RequestBody LikeDto likeDto) {
        log.info("Request for add like to post: {} by user: {}", likeDto.getPostId(), likeDto.getUserId());
        ResponseLikeDto responseLikeDto = likeService.addLikeToPost(likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseLikeDto);
    }
}
