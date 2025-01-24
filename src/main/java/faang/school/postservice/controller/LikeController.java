package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LikeController {
    private static final String USER_PATH_POST = "/user/post";
    private static final String USER_PATH_COMMENT = "/user/post/comment";
    private static final String REMOVE = "/remove/{likeId}";

    private final LikeService likeService;

    @PostMapping(USER_PATH_POST)
    public LikePostDto userLikeThePost(@RequestBody LikePostDto dto) {
        return likeService.userLikeThePost(dto);
    }

    @PostMapping(USER_PATH_COMMENT)
    public LikeCommentDto userLikeTheComment(@RequestBody LikeCommentDto dto) {
        return likeService.userLikeTheComment(dto);
    }

    @DeleteMapping(USER_PATH_COMMENT + REMOVE)
    public LikeCommentDto removeLikeFromComment(@PathVariable Long likeId, @RequestBody LikeCommentDto dto) {
        return likeService.removeLikeComment(likeId, dto);
    }

    @DeleteMapping(USER_PATH_POST + REMOVE)
    public LikePostDto removeLikeFromPost(@PathVariable Long likeId, @RequestBody LikePostDto dto) {
        return likeService.removeLikePost(likeId, dto);
    }
}
