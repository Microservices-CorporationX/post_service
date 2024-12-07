package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.utilities.UrlUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1)
public class LikeController {
    private final LikeService likeService;

    @PostMapping(UrlUtils.POSTS + UrlUtils.ID + UrlUtils.LIKE)
    public LikeDto likePost(@PathVariable @Min(1) long id, @Valid @RequestBody LikeDto like) {
        return likeService.likePost(id, like);
    }

    @DeleteMapping(UrlUtils.POSTS + UrlUtils.ID + UrlUtils.LIKE)
    public void unlikePost(@PathVariable @Min(1) long id, @Valid @RequestBody LikeDto like) {
        likeService.unlikePost(id, like);
    }

    @PostMapping(UrlUtils.COMMENT + UrlUtils.COMMENT_ID + UrlUtils.LIKE)
    public LikeDto likeComment(@PathVariable @Min(1) long commentId, @Valid @RequestBody LikeDto like) {
        return likeService.likeComment(commentId, like);
    }

    @DeleteMapping(UrlUtils.COMMENT + UrlUtils.COMMENT_ID + UrlUtils.LIKE)
    public void unlikeComment(@PathVariable @Min(1) long commentId, @Valid @RequestBody LikeDto like) {
        likeService.unlikeComment(commentId, like);
    }
}
