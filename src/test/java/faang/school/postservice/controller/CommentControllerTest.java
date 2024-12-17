package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentControllerValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final CommentDto commentDto = getCommentDto();

    @Test
    void createComment() {
        commentController.createComment(commentDto);
        Mockito.verify(commentService).createComment(commentDto);
    }

    @Test
    void updateComment() {
        commentController.updateComment(commentDto);
        Mockito.verify(commentService).updateComment(commentDto);
    }

    @Test
    void getCommentsByPostId() {
        commentController.getCommentsByPostId(commentDto.getPostId());
        Mockito.verify(commentService).getCommentsByPostId(commentDto.getPostId());
    }

    @Test
    void deleteComment() {
        commentController.deleteComment(commentDto.getId());
        Mockito.verify(commentService).deleteComment(commentDto.getId());
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Content 1")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}