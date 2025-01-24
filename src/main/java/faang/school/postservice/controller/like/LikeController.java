package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final LikeMapper likeMapper;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto createLikePost(@PathVariable Long postId) {
        Like like = likeService.saveLikePost(postId);
        return likeMapper.toDto(like);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto createLikeComment(@PathVariable @Positive Long commentId) {
        Like like = likeService.saveLikeComment(commentId);
        return likeMapper.toDto(like);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikePost(@PathVariable @Positive Long postId) {
        likeService.deleteLikePost(postId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeComment(@PathVariable @Positive Long commentId) {
        likeService.deleteLikeComment(commentId);
    }

    @GetMapping("/postCountLikes/{postId}")
    public Long getCountLikesPost(@PathVariable @Positive Long postId) {
        return likeService.countLikesPost(postId);
    }
}
