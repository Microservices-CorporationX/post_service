package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.likes.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CheckException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepositoryAdapter;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.LikeRepositoryAdapter;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final LikeRepositoryAdapter likeRepositoryAdapter;
    private final CommentRepositoryAdapter commentRepositoryAdapter;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    @Override
    public LikeDto likePost(long userId, long postId) {
        Post post = postRepositoryAdapter.findById(postId);
        Like postLike = likeRepositoryAdapter.findLikeByPostIdAndUserId(userId, postId);
        if (postLike != null) {
            throw new CheckException("Вы уже поставили лайк на этот пост!");
        }
        postLike = new Like();
        postLike.setUserId(userId);
        postLike.setPost(post);
        return likeMapper.toDto(likeRepository.save(postLike));
    }

    @Transactional
    @Override
    public void deletePostLike(long userId, long postId) {
        Like like = likeRepositoryAdapter.findLikeByPostIdAndUserId(userId, postId);
        if (like == null) {
            throw new CheckException("Лайк не найден!");
        }
        likeRepository.delete(like);
    }

    @Transactional
    @Override
    public LikeDto likeComment(long userId, long commentId) {
        Comment comment = commentRepositoryAdapter.findById(commentId);
        Like commentLike = likeRepositoryAdapter.findLikeByCommentIdAndUserId(userId, commentId);
        if (commentLike != null) {
            throw new CheckException("Вы уже поставили лайк на этот комментарий!");
        }
        commentLike = new Like();
        commentLike.setUserId(userId);
        commentLike.setComment(comment);
        return likeMapper.toDto(likeRepository.save(commentLike));
    }

    @Transactional
    @Override
    public void deleteCommentLike(long userId, long commentId) {
        Like like = likeRepositoryAdapter.findLikeByCommentIdAndUserId(userId, commentId);
        if (like == null) {
            throw new CheckException("Лайк не найден!");
        }
        likeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> usersByPostId(long postId) {
        postRepositoryAdapter.findById(postId);
        List<Like> likes = likeRepository.findLikesByPostId(postId);
        if (!likes.isEmpty()) {
            return getUsersByUserIds(likes);
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> usersByCommentId(long commentId) {
        commentRepositoryAdapter.findById(commentId);
        List<Like> likes = likeRepository.findLikesByCommentId(commentId);
        if (!likes.isEmpty()) {
            return getUsersByUserIds(likes);
        }
        return Collections.emptyList();
    }

    private List<UserDto> getUsersByUserIds(List<Like> likes) {
        List<Long> ids = likes.stream().map(Like::getUserId).collect(Collectors.toList());
        List<UserDto> users = new ArrayList<>();
        int batchSize = 100;
        while (ids.size() > batchSize) {
            List<Long> batch = ids.stream().limit(batchSize).toList();
            users.addAll(userServiceClient.getUsersByIds(batch));
            ids.removeAll(batch);
        }
        if (!ids.isEmpty()) {
            users.addAll(userServiceClient.getUsersByIds(ids));
        }
        return users;
    }
}
