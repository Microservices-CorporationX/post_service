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

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPostLike(@Valid @RequestBody LikeDto dto) {
        likeService.createPostLike(dto);
    }

    @DeleteMapping("/{postId}/post")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePostLike(@PathVariable long postId) {
        likeService.removePostLike(postId);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCommentLike(@Valid @RequestBody LikeDto dto) {
        likeService.createCommentLike(dto);
    }

    @DeleteMapping("/{commentId}/comment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentLike(@PathVariable long commentId) {
        likeService.removeCommentLike(commentId);
    }
}
