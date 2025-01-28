package faang.school.postservice.service;

import faang.school.postservice.dto.likes.LikeDto;

public interface LikeService {
    LikeDto likePost(long userId, long postId);

    void deletePostLike(long userId, long postId);

    LikeDto likeComment(long userId, long commentId);

    void deleteCommentLike(long userId, long commentId);
}
