package faang.school.postservice.service.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public void verifyDtoParams(LikeDto dto) {
        findUserById(dto.userId());

        if (dto.postId() != null) {
            var post = findPostById(dto.postId());
            verifyPostLikeExists(post, dto);

        } else {
            var comment = findCommentById(dto.commentId());
            verifyCommentLikeExists(comment, dto);
        }

    }

    public void verifyPostLikeExists(Post post, LikeDto dto) {
        post.getLikes()
                .stream()
                .filter(likeItem -> likeItem.getPost().getId().equals(dto.postId()))
                .findAny()
                .ifPresent(like -> {
                    throw new BusinessException("Нельзя повторно ставить лайк на пост с ID " + dto.postId());
                });
    }

    public void verifyCommentLikeExists(Comment comment, LikeDto dto) {
        comment.getLikes()
                .stream()
                .filter(likeItem -> likeItem.getComment().getId().equals(dto.commentId()))
                .findAny()
                .ifPresent(like -> {
                    throw new BusinessException("Нельзя повторно ставить лайк на комментарий с ID " + dto.commentId());
                });
    }

    public Post findPostById(long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));
    }

    public UserDto findUserById(long userId) {
        try {
            userContext.setUserId(userId);
            return userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }

    public Comment findCommentById(long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с ID " + commentId + " не найден"));
    }

}
