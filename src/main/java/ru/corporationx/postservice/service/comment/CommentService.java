package ru.corporationx.postservice.service.comment;

import ru.corporationx.postservice.dto.comment.CommentDto;
import ru.corporationx.postservice.dto.comment.CommentEvent;
import ru.corporationx.postservice.exception.DataValidationException;
import ru.corporationx.postservice.mapper.comment.CommentMapper;
import ru.corporationx.postservice.model.Comment;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.publisher.comment.CommentEventPublisher;
import ru.corporationx.postservice.repository.CommentRepository;
import ru.corporationx.postservice.service.post.PostService;
import ru.corporationx.postservice.validator.comment.CommentValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentValidator commentValidator;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CommentEventPublisher commentEventPublisher;

    public Comment findEntityById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Comment with id '%s' not found", id)));
    }

    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.validateCreation(commentDto);
        Post post = postService.findEntityById(commentDto.getPostId());

        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setUpdatedAt(LocalDateTime.now());

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        comment = commentRepository.save(comment);

        publishCommentCreationEvent(comment);

        return commentMapper.toDto(comment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        if (commentDto.getId() == null) {
            throw new DataValidationException("Comment id is required for update");
        }
        Comment comment = findEntityById(commentDto.getId());
        commentValidator.validateUpdate(comment, commentDto);
        commentDto.setUpdatedAt(LocalDateTime.now());

        commentMapper.update(comment, commentDto);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getComments(long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void publishCommentCreationEvent(Comment comment) {
        commentEventPublisher.publish(new CommentEvent(
                comment.getAuthorId(),
                comment.getPost().getId(),
                comment.getId(),
                comment.getContent()
        ));
    }
}
