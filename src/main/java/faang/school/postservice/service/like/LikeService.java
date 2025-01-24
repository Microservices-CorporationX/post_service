package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;

    @Transactional
    public Like saveLikePost(long postId) {
        long userId = getUserId();
        validatePost(postId);
        validateLikePost(postId, userId);
        Post post = postService.getPostById(postId);
        Like like = getLikePost(post, userId);
        return likeRepository.save(like);
    }

    @Transactional
    public Like saveLikeComment(long commentId) {
        long userId = getUserId();
        validateComment(commentId);
        validateLikeComment(commentId, userId);
        Comment comment = commentService.getComment(commentId);
        Like like = getLikeComment(comment, userId);

        return likeRepository.save(like);
    }

    @Transactional
    public void deleteLikePost(long postId) {
        long userId = getUserId();
        if (likeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
            throw new EntityExistsException(String.format("Like not exists for postId: %d", postId));
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public void deleteLikeComment(long commentId) {
        long userId = getUserId();
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty()) {
            throw new EntityExistsException(String.format("Like not exists for commentId: %d", commentId));
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public Long countLikesPost(Long postId) {
        validatePost(postId);
        if (!postService.getPostById(postId).isPublished()) {
            throw new IllegalArgumentException("Post is not published");
        }
        return postService.getPostById(postId).getLikes().stream()
            .filter(Objects::nonNull)
            .count();
    }

    private void validatePost(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post not found with id: %d", postId));
        }
    }

    private void validateComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format("Comment not found with id: %d", commentId));
        }
    }

    private void validateLikePost(long postId, long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new EntityExistsException(String.format("Like already exists for postId: %d", postId));
        }
    }

    private void validateLikeComment(long commentId, long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new EntityExistsException(String.format("Like already exists for commentId: %d", commentId));
        }
    }

    private long getUserId() {
        long userId = userContext.getUserId();
        return userServiceClient.getUser(userId).id();
    }

    private Like getLikePost(Post post, long userId) {
        Like like = new Like();
        like.setPost(post);
        like.setUserId(userId);
        return like;
    }

    private Like getLikeComment(Comment comment, long userId) {
        Like like = new Like();
        like.setComment(comment);
        like.setUserId(userId);
        return like;
    }
}
