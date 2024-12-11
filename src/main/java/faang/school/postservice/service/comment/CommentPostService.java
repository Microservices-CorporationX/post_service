package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;

import java.util.List;

public interface CommentPostService {

    List<Comment> findCommentsByPostId(Long id);

    Comment getById(Long commentId);

    Comment create(Long postId, Comment comment);

    Comment update(Long postId, Comment comment);

    void delete(Long commentId);

    void deleteAll(Long postId);
}
