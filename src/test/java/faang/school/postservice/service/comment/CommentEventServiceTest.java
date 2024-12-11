package faang.school.postservice.service.comment;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostDaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("service CommentEventService")

class CommentEventServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostDaoServiceImpl postService;
    @InjectMocks
    private CommentPostServiceImpl commentService;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private Long postId;
    private Long commentId;
    private Long authorId;
    private String content;
    private Comment someComment;
    private Comment newComment;

    @BeforeEach
    void start() {
        postId = new Random().nextLong(1, 1000);
        commentId = new Random().nextLong(1, 1000);
        authorId = new Random().nextLong(1, 1000);
        content = "some content";
        String newContent = " some new content";
        someComment = Comment.builder().id(commentId).content(content).authorId(authorId).build();
        newComment = Comment.builder().content(newContent).build();
    }

    @Test
    void testCreateComment() {
        Long postAuthorId = new Random().nextLong(1, 1000);
        Post post = Post.builder().id(postId).authorId(postAuthorId).build();
        Comment commentSaved = Comment.builder().id(commentId).authorId(authorId).content(content).post(post).build();
        when(postService.getById(postId)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(commentSaved);
        Comment result = commentService.create(postId, someComment);
        verify(commentRepository).save(someComment);
        assertEquals(commentSaved, result);
    }

    @Test
    void testUpdateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(someComment));
        String newContent = "Abra cada bra";
        someComment.setContent(newContent);
        commentService.update(commentId, someComment);
        assertEquals(commentId, someComment.getId());
        assertEquals(newContent, someComment.getContent());
        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(commentId, commentCaptor.getValue().getId());
        assertEquals(newContent, commentCaptor.getValue().getContent());
    }

    @Test
    void testUpdateCommentThrowsValidationException() {
        Long notExistCommentId = new Random().nextLong(1000, 2000);
        when(commentRepository.findById(notExistCommentId)).thenReturn(Optional.ofNullable(someComment));
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.update(commentId, newComment)
        );
    }

    @Test
    void testGetById() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(someComment));
        var real = commentService.getById(commentId);
        assertEquals(someComment, real);
    }

    @Test
    void testGetAllCommentsByPostId() {
        var comment1 = Comment.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(1024, 1, 1, 1, 1, 1))
                .build();
        var comment2 = Comment.builder()
                .id(2L)
                .createdAt(LocalDateTime.of(2024, 2, 2, 2, 1, 1))
                .build();
        var comment3 = Comment.builder()
                .id(3L)
                .createdAt(LocalDateTime.of(3024, 3, 3, 3, 1, 1))
                .build();
        List<Comment> comments = List.of(comment1, comment2, comment3);
        List<Comment> sortedComments = comments.stream()
                .sorted(
                        (c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt())
                )
                .toList();
        when(commentRepository.findByPostId(postId)).thenReturn(comments);
        var real = commentService.findCommentsByPostId(postId);
        assertEquals(sortedComments, real);
    }

    @Test
    void testDelete() {
        Long notExistCommentId = new Random().nextLong(1000, 2000);
        when(commentRepository.findById(notExistCommentId)).thenReturn(Optional.ofNullable(someComment));
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.delete(commentId)
        );
    }
}