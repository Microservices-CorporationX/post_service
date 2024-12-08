package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    private Comment comment ;

    private List<Comment> commentList;

    @BeforeEach
    void SetUp(){
        comment = new Comment();
        comment.setId(1);

        commentList = new ArrayList<>();
        commentList.add(comment);

    }

    @Test
    public void testGetCommentNotFound() {
        long id = 1L;
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getComment(id));
    }

    @Test
    public void testGetComment() {
        long id = 1L;

        when(commentRepository.findById(id)).thenReturn(Optional.of(new Comment()));
        commentService.getComment(id);
    }

    @Test
    public void testExistsCommentByIdWhenIdIsNull() {
        assertFalse(commentService.existsCommentById(null));
    }

    @Test
    public void testExistsComment() {
        long id = 1L;
        when(commentRepository.existsById(id)).thenReturn(true);
        assertTrue(commentService.existsCommentById(id));
    }

    @Test
    void testCommentServiceCallingCommentRepository(){
        when(commentRepository.getAllNotVerified()).thenReturn(commentList);

        List<Comment> result = commentService.getAllNotVerifiedComments();

        assertEquals(result,commentList);
    }
    @Test
    void testMarkAsRemovedUnVerifiedCommentsCallingCommentRepository(){
        commentService.markAsRemovedUnVerifiedComments();

        verify(commentRepository).markAsRemovedAllUnVerifiedComments();
    }
}
