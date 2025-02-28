package ru.corporationx.postservice.service.like;

import ru.corporationx.postservice.dto.like.LikeDto;
import ru.corporationx.postservice.kafka.producer.KafkaLikeProducer;
import ru.corporationx.postservice.mapper.like.LikeMapper;
import ru.corporationx.postservice.model.Comment;
import ru.corporationx.postservice.model.Like;
import ru.corporationx.postservice.model.Post;
import ru.corporationx.postservice.repository.CommentRepository;
import ru.corporationx.postservice.repository.LikeRepository;
import ru.corporationx.postservice.repository.PostRepository;
import ru.corporationx.postservice.service.comment.CommentService;
import ru.corporationx.postservice.service.post.PostService;
import ru.corporationx.postservice.validator.like.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final PostService postService;
    private final KafkaLikeProducer<LikeDto> kafkaLikeProducer;

    public LikeDto likeComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validateComment(commentId, likeDto);
        likeValidator.validateWhereIsLikePlaced(likeDto);

        Comment comment = commentService.findEntityById(commentId);
        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        like.setPost(comment.getPost());
        comment.getLikes().add(like);

        likeRepository.save(like);

        return likeMapper.toDto(like);
    }

    public LikeDto likePost(Long postId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validatePost(postId, likeDto);
        likeValidator.validateWhereIsLikePlaced(likeDto);

        Post post = postService.findEntityById(postId);
        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        like.setComment(null);
        post.getLikes().add(like);

        likeRepository.save(like);

        sendMessageToKafka(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto removeLikeUnderComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validateComment(commentId, likeDto);
        likeValidator.validateLike(likeDto.getId());

        Comment comment = commentService.findEntityById(commentId);
        Like like = likeMapper.toEntity(likeDto);

        likeRepository.deleteByCommentIdAndUserId(commentId, like.getUserId());
        comment.getLikes().removeIf(existingLike -> existingLike.getUserId().equals(like.getUserId()));
        commentRepository.save(comment);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto removeLikeUnderPost(Long postId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validatePost(postId, likeDto);
        likeValidator.validateLike(likeDto.getId());

        Post post = postService.findEntityById(postId);
        Like like = likeMapper.toEntity(likeDto);

        likeRepository.deleteByPostIdAndUserId(postId, like.getUserId());
        post.getLikes().removeIf(existingLike -> existingLike.getUserId().equals(like.getUserId()));
        postRepository.save(post);

        return likeMapper.toDto(like);
    }

    private void sendMessageToKafka(Like like) {
        kafkaLikeProducer.send(LikeDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .postId(like.getPost().getId())
                .createdAt(like.getCreatedAt())
                .build());
    }
}
