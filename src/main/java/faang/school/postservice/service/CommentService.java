package faang.school.postservice.service;

import faang.school.postservice.dto.comment.ReadCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public ReadCommentDto create(CreateCommentDto createDto) {
        userService.checkUserExists(createDto.getAuthorId());

        Comment newComment = commentMapper.toEntity(createDto);
        newComment = commentRepository.save(newComment);
        return commentMapper.toDto(newComment);
    }

    public ReadCommentDto update(UpdateCommentDto updateDto) {
        Comment comment = getCommentById(updateDto.getId());

        validateEditorAndAuthorEquality(updateDto.getEditorId(), comment.getAuthorId());

        commentMapper.updateEntityFromDto(updateDto, comment);
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    public List<ReadCommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public void remove(long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментария с ID " + commentId + " не найден"));
    }

    private void validateEditorAndAuthorEquality (long editorId, long authorId) {
        if (editorId != authorId) {
            throw new BusinessException("Редактировать комментарий может только его автор");
        }
    }

}
