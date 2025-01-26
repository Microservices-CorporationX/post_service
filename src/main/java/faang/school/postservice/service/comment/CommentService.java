package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;

    private static final String TOPIC = "comment-created-events-topic";
    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        log.info("Creating a comment for post ID: {} by user ID: {}", commentDto.getPostId(), commentDto.getAuthorId());

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalStateException("Post not found with ID: " + commentDto.getPostId()));

        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        log.info("Getting user author comment : {}", userDto);

        if (userDto == null) {
            throw new IllegalStateException("User not found with ID: " + commentDto.getAuthorId());
        }

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created with ID: {}", savedComment.getId());

        CommentEvent commentEvent = new CommentEvent(
                commentDto.getPostId(),
                post.getAuthorId(),
                commentDto.getAuthorId(),
                comment.getId(),
                LocalDateTime.now());

        commentEventPublisher.publishMessage(commentEvent);

        String commentId = Long.toString(savedComment.getId());

        CompletableFuture<SendResult<String, CommentEvent>> future = kafkaTemplate
                .send(TOPIC, commentId, commentEvent);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Failed to send message: {}", exception.getMessage());
            } else {
                log.info("Message sent successfully: {}", result.getRecordMetadata());
                log.info("Topic createComment: {}", result.getRecordMetadata().topic());
                log.info("Partition createComment: {}", result.getRecordMetadata().partition());
                log.info("Offset createComment: {}", result.getRecordMetadata().offset());
            }

        });

        log.info("Return createComment: {} {}", commentId, comment);

        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(long commentId, CommentDto commentDto) {
        log.info("Updating comment with ID: {}", commentId);

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalStateException("Comment not found with ID: " + commentId));

        if (!existingComment.getAuthorId().equals(commentDto.getAuthorId())) {
            throw new IllegalArgumentException("Only the author of the comment can update it.");
        }

        if (commentDto.getContent() != null && !commentDto.getContent().isBlank()) {
            existingComment.setContent(commentDto.getContent());
            existingComment.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("Content cannot be blank.");
        }

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment with ID: {} updated successfully.", commentId);

        return commentMapper.toDto(updatedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(long postId) {
        log.info("Fetching comments for post ID: {} in chronological order", postId);
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteComment(long commentId) {
        log.info("Deleting comment with ID: {}", commentId);

        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }

        commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} has been deleted.", commentId);
    }
}
