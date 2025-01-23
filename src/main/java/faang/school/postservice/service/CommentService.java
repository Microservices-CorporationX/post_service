package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public Comment createComment(Comment comment, Long postId) {
        getUser(comment.getAuthorId());
        Post post = postService.getPostById(postId);
        post.getComments().add(comment);
        postService.savePost(post);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {
        Comment savedComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new NoSuchElementException("Comment not found or deleted"));

        savedComment.setContent(comment.getContent());
        savedComment.setUpdatedAt(LocalDateTime.now());

        return savedComment;
    }

    public List<Comment> getAllCommentsToPost(Long postId) {
        Post post = postService.getPostById(postId);
        return post.getComments().stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void getUser(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (Exception e) {
            throw new NoSuchElementException(String.format("User with ID#%s not found", authorId));
        }
    }
}
