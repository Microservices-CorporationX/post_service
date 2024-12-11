package faang.school.postservice.service.comment;

import faang.school.postservice.annotations.PublishEvent;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.CommentPostEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostDaoServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentPostServiceImpl implements CommentPostService {

    private final PostDaoServiceImpl postService;
    private final CommentRepository commentRepository;

    @Override
    public List<Comment> findCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    @Override
    public Comment getById(Long commentId) {
        var existingComment = commentRepository.findById(commentId);
        if (existingComment.isEmpty()) {
            throw new ResourceNotFoundException("NO COMMENT PRESENT WITH ID = " + commentId);
        }
        return existingComment.get();
    }

    @Override
    @PublishEvent(eventType = CommentPostEvent.class)
    public Comment create(Long postId, Comment comment) {
        Post post;
        try {
            post = postService.findPostById(postId);
        } catch (Exception exception) {
            throw new ResourceNotFoundException("NO OBJECT PRESENT WITH ID = " + exception.getMessage());
        }
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(Long commentId, Comment comment) {
        var existingComment = commentRepository.findById(commentId);
        if (existingComment.isEmpty()) {
            throw new ResourceNotFoundException("NO COMMENT PRESENT WITH ID = " + comment.getId());
        }
        comment.setContent(comment.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public void delete(Long commentId) {
        var existingComment = commentRepository.findById(commentId);
        if (existingComment.isEmpty()) {
            throw new ResourceNotFoundException("NO COMMENT PRESENT WITH ID = " + commentId);
        }
        commentRepository.delete(existingComment.get());
    }

    @Override
    public void deleteAll(Long postId) {
        try {
            postService.findPostById(postId);
        } catch (Exception exception) {
            throw new ResourceNotFoundException("NO OBJECT PRESENT WITH ID = " + exception.getMessage());
        }

        List<Comment> comments = commentRepository.findByPostId(postId);
        if (comments.isEmpty()) {
            log.info("No elements for deleting found!");
        } else
            commentRepository.deleteAll(comments);
    }
}
