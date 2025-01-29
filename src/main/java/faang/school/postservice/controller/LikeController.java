package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersLikedToPost(@Valid @NotNull @PathVariable Long postId) {
        return likeService.getLikedUsersToPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersLikedToComment(@Valid @NotNull @PathVariable Long commentId) {
        return likeService.getLikedUsersToComment(commentId);
    }

}

