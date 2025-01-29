package faang.school.postservice.service.comment;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.redis.service.AuthorCacheService;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentServiceValidator validator;
    private final PostService postService;
    private final AuthorCacheService authorCacheService;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final CommentEventMapper commentEventMapper;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateCreateComment(commentDto);

        Post post = postService.findPostById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(commentDto.getPostId())));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        CommentEvent commentEvent = commentEventMapper.toDto(commentRepository.save(comment));
        authorCacheService.saveAuthorCache(commentEvent.getAuthorId());
        kafkaCommentProducer.sendCommentEvent(commentEvent);

        return commentDto;
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(commentDto.getId())));
        commentMapper.update(commentDto, comment);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        validator.validatePostId(postId);
        return commentMapper.toDto(commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList()
        );
    }

    @Override
    public void deleteComment(Long commentId) {
        validator.validateCommentId(commentId);
        commentRepository.deleteById(commentId);
    }
}
