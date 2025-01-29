package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exceptions.CommentWasNotFoundException;
import faang.school.postservice.exceptions.PostWasNotFoundException;
import faang.school.postservice.exceptions.UserServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
            log.error("Post with id {} does not exist", postId);
            throw new PostWasNotFoundException("Post with id %s does not exist".formatted(postId));
        }
        var ids = likeRepository.findAllByPostId(postId)
                .map(Like::getUserId)
                .collect(Collectors.toList());

        return getUsersFromUserService(ids);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getLikedUsersToComment(long commentId) {
        if (!commentService.existsById(commentId)) {
            log.error("Comment with id {} does not exist", commentId);
            throw new CommentWasNotFoundException("Comment with id %s does not exist".formatted(commentId));
        }
        var ids = likeRepository.findAllByCommentId(commentId)
                .map(Like::getUserId)
                .collect(Collectors.toList());

        return getUsersFromUserService(ids);
    }

    private List<UserDto> getUsersFromUserService(List<Long> ids) {
        try {
            return userServiceClient.getUsersByIds(ids);
        } catch (Exception e) {
            log.error("Failed to get users from users service", e);
            throw new UserServiceConnectException("Failed users service");
        }
    }
}
