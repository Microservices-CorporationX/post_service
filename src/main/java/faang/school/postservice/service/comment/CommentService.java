package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(long postId, CommentRequestDto commentDto);

    CommentResponseDto updateComment(long commentId, CommentUpdateDto commentUpdateDto);

    List<CommentResponseDto> getComments(long postId);

    void deleteComment(long commentId);
}
