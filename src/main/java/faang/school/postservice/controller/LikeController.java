package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto createPostLike(@PathVariable Long postId, @Valid @RequestBody LikeDto dto) {
        return likeService.createPostLike(postId, dto);
    }

    @DeleteMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePostLike(@PathVariable Long postId, @Valid @RequestBody LikeDto dto) {
        likeService.removePostLike(postId, dto);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto createCommentLike(@PathVariable Long commentId, @Valid @RequestBody LikeDto dto) {
        return likeService.createCommentLike(commentId, dto);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentLike(@PathVariable Long commentId, @Valid @RequestBody LikeDto dto) {
        likeService.removeCommentLike(commentId, dto);
    }
}
