package ru.corporationx.postservice.validator.like;

import ru.corporationx.postservice.client.UserServiceClient;
import ru.corporationx.postservice.dto.like.LikeDto;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.repository.CommentRepository;
import ru.corporationx.postservice.repository.LikeRepository;
import ru.corporationx.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public boolean validateWhereIsLikePlaced(@Valid LikeDto likeDto) {
        if (likeDto.getCommentId() != null && likeDto.getPostId() != null) {
            log.error("Trying to like a post and a comment at the same time");
            throw new DataValidationException("You can't like a post and a comment at the same time");
        }
        if (likeDto.getPostId() != null && likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()).isPresent()) {
            log.error("Attempt to like the same post by the same user");
            throw new DataValidationException("The same user cannot like the same post");
        }
        if (likeDto.getCommentId() != null && likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()).isPresent()) {
            log.error("Attempt to like the same comment by the same user");
            throw new DataValidationException("The same user cannot like the same comment");
        }
        return true;
    }

    public boolean validateComment(long commentId, @Valid LikeDto likeDto) {
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment with ID {} does not exist", commentId);
            throw new DataValidationException("Comment does not exist");
        }
        if (likeDto.getId() != null && likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()).isEmpty()) {
            log.error("Like on comment with ID {} from user with ID {} does not exist", likeDto.getCommentId(), likeDto.getUserId());
            throw new DataValidationException("Like does not exist");
        }
        return true;
    }

    public boolean validatePost(long postId, @Valid LikeDto likeDto) {
        if (!postRepository.existsById(postId)) {
            log.error("Post with ID {} does not exist", postId);
            throw new DataValidationException("Post does not exist");
        }
        if (likeDto.getId() != null && likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()).isEmpty()) {
            log.error("Like on post with ID {} from user with ID {} does not exist", postId, likeDto.getUserId());
            throw new DataValidationException("Like does not exist");
        }
        return true;
    }

    public boolean validateLike(long likeId) {
        if (!likeRepository.existsById(likeId)) {
            log.error("Like with ID {} does not exist", likeId);
            throw new DataValidationException("Like does not exist");
        }
        return true;
    }

    public boolean validateUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error("User with ID {} does not exist", userId);
            throw new DataValidationException("User does not exist");
        }
        return true;
    }
}
