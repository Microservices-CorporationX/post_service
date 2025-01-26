package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exceptions.UserServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional(readOnly = true)
    public List<UserDto> getLikedUsersToPost(long postId) {
        if (!postService.existsById(postId)) {
            throw new IllegalArgumentException("Post with id %s does not exist".formatted(postId));
        }

        var ids = likeRepository.findAllByPostId(postId)
                .map(Like::getUserId)
                .collect(Collectors.toList());

        try {
            return userServiceClient.getUsersByIds(ids);
        } catch (Exception e) {
            throw new UserServiceConnectException("Failed users service");
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getLikedUsersToComment(long commentId) {
        if (!commentService.existsById(commentId)) {
            throw new IllegalArgumentException("Comment with id %s does not exist".formatted(commentId));
        }

        var ids = likeRepository.findAllByCommentId(commentId)
                .map(Like::getUserId)
                .collect(Collectors.toList());

        try {
            return userServiceClient.getUsersByIds(ids);
        } catch (Exception e) {
            throw new UserServiceConnectException("Failed users service");
        }
    }
}
