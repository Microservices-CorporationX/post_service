package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/comments/{commentId}/likes")
    public LikeDto likeComment(@PathVariable @NotNull @Positive Long commentId, @RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getId() != null) {
            throw new DataValidationException("The like must not contain an ID for creation");
        }
        return likeService.likeComment(commentId, likeDto);
    }

    @PostMapping("/posts/{postId}/likes")
    public LikeDto likePost(@PathVariable @NotNull @Positive Long postId, @RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getId() != null) {
            throw new DataValidationException("The like must not contain an ID for creation");
        }
        return likeService.likePost(postId, likeDto);
    }

    @DeleteMapping("/comments/{commentId}/likes")
    public LikeDto removeLikeUnderComment(@PathVariable @Positive long commentId, @RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getId() == null) {
            throw new DataValidationException("The like must contain ID to be removed");
        }
        return likeService.removeLikeUnderComment(commentId, likeDto);
    }

    @DeleteMapping("/posts/{postId}/likes")
    public LikeDto removeLikeUnderPost(@PathVariable @Positive long postId, @RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getId() == null) {
            throw new DataValidationException("The like must contain ID to be removed");
        }
        return likeService.removeLikeUnderPost(postId, likeDto);
    }
}
