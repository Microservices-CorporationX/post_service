package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto createComment(long postId, CommentRequestDto commentDto) {
        return commentMapper.toCommentResponseDto(new Comment());
    }

    @Override
    public CommentResponseDto updateComment(long commentId, CommentUpdateDto commentUpdateDto) {
        return commentMapper.toCommentResponseDto(new Comment());
    }

    @Override
    public List<CommentResponseDto> getComments(long postId) {
        return new ArrayList<>();
    }

    @Override
    public void deleteComment(long commentId) {

    }
}
