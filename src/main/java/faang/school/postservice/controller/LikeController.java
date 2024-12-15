package faang.school.postservice.controller;

import faang.school.postservice.docs.like.CreateLikeDoc;
import faang.school.postservice.docs.like.GetLikersByCommentIdDoc;
import faang.school.postservice.docs.like.GetLikersByPostIdDoc;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeResponseDto;
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

    @GetLikersByPostIdDoc
    @GetMapping("/posts/{postId}/likers")
    public ResponseEntity<List<UserDto>> getUsersWhoLikePostByPostId(
            @PathVariable
            @Positive(message = "Post id must be positive")
            Long postId
    ) {
        log.info("Request for users by post id: {}", postId);
        return ResponseEntity.ok(likeService.getUsersWhoLikePostByPostId(postId));
    }

    @GetLikersByCommentIdDoc
    @GetMapping("/comments/{commentId}/likers")
    public ResponseEntity<List<UserDto>> getUsersWhoLikeComments(
            @PathVariable
            @Positive(message = "Comment id must be positive")
            Long commentId
    ) {
        log.info("Request for users by comment id: {}", commentId);
        return ResponseEntity.ok(likeService.getUsersWhoLikeComments(commentId));
    }

    @CreateLikeDoc
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<LikeResponseDto> addLikeToPost(
            @PathVariable
            @Positive(message = "Post id must be positive")
            Long postId,
            @Valid @RequestBody LikeDto likeDto
    ) {
        log.info("Request for add like to post: {} by user: {}", postId, likeDto.getUserId());
        LikeResponseDto likeResponseDto = likeService.addLikeToPost(postId, likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponseDto);
    }
}
