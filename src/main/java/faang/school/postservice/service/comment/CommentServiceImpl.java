package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.redis.repository.UserCacheRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final CommentServiceValidator validator;

    private final PostService postService;

    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaCommentProducer kafkaCommentProducer;


    @Override
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateCreateComment(commentDto);

        Post post = postService.findPostById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(commentDto.getPostId())));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        Comment storedComment = commentRepository.save(comment);

        saveUserToCache(comment.getAuthorId());
        produceCommentCreationMessage(commentDto);

        return commentMapper.toDto(storedComment);
    }

    private void saveUserToCache(long userId) {
        UserDto userDto = userServiceClient.getUserById(userId);
        log.info("Saving user %s to cache when comment made = {}", userDto);
        userCacheRepository.cacheUser(userDto);
    }

    private void produceCommentCreationMessage(CommentDto comment) {
        log.info("Producing comment creation message = {}", comment);
        kafkaCommentProducer.sendMessage(comment);
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
