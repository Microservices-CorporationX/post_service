package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private static final String ERROR_USER_NOT_FOUND = "User does not exist";
    private static final String ERROR_POST_NOT_FOUND = "Post does not exist";
    private static final String ERROR_COMMENT_NOT_FOUND = "Comment does not exist";
    private static final String ERROR_LIKE_NOT_FOUND = "Like does not exist";
    private static final String ERROR_USER_LIKED = "User already liked this entity";

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto likePost(long postId, @Valid LikeDto like) {
        validateUserExists(like.getUserId());
        Post post = getPostOrThrow(postId);
        checkForDuplicateLike(likeRepository.findByPostIdAndUserId(postId, like.getUserId()));

        return likeMapper.toDto(mapAndSaveLike(like.getUserId(), post));
    }

    @Transactional
    public LikeDto likeComment(long commentId, @Valid LikeDto like) {
        validateUserExists(like.getUserId());
        Comment comment = getCommentOrThrow(commentId);
        checkForDuplicateLike(likeRepository.findByCommentIdAndUserId(commentId, like.getUserId()));

        return likeMapper.toDto(mapAndSaveLike(like.getUserId(), comment));
    }

    @Transactional
    public void unlikePost(long postId, @Valid LikeDto like) {
        validateUserExists(like.getUserId());
        validatePostExists(postId);
        validatePostLikeExists(postId, like.getUserId());
        likeRepository.deleteByPostIdAndUserId(postId, like.getUserId());
    }

    @Transactional
    public void unlikeComment(long commentId, @Valid LikeDto like) {
        validateUserExists(like.getUserId());
        validateCommentExists(commentId);
        validateCommentLikeExists(commentId, like.getUserId());
        likeRepository.deleteByCommentIdAndUserId(commentId, like.getUserId());
    }

    private void validateUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new IllegalArgumentException(ERROR_USER_NOT_FOUND, e);
        }
    }

    private void checkForDuplicateLike(Optional<Like> existingLike) {
        if (existingLike.isPresent()) {
            throw new IllegalArgumentException(ERROR_USER_LIKED);
        }
    }

    private void validatePostExists(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException(ERROR_POST_NOT_FOUND);
        }
    }

    private void validatePostLikeExists(long postId, long userId) {
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalArgumentException(ERROR_LIKE_NOT_FOUND);
        }
    }

    private void validateCommentExists(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException(ERROR_COMMENT_NOT_FOUND);
        }
    }

    private void validateCommentLikeExists(long commentId, long userId) {
        if (!likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException(ERROR_LIKE_NOT_FOUND);
        }
    }

    private Like mapAndSaveLike(Long userId, Object entity) {
        Like likeEntity = new Like();
        likeEntity.setUserId(userId);

        if (entity instanceof Post) {
            likeEntity.setPost((Post) entity);
        } else if (entity instanceof Comment) {
            likeEntity.setComment((Comment) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }

        likeRepository.save(likeEntity);
        return likeEntity;
    }

    private Post getPostOrThrow(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_POST_NOT_FOUND));
    }

    private Comment getCommentOrThrow(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_COMMENT_NOT_FOUND));
    }
}