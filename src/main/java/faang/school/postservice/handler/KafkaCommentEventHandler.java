package faang.school.postservice.handler;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostRedisMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@KafkaListener(
        topics = {"${spring.data.kafka.topics.comments.name:default-topic}"},
        groupId = "${spring.kafka.consumer.group-id:default-group}",
        concurrency = "${spring.kafka.consumer.concurrency}")
@RequiredArgsConstructor
public class KafkaCommentEventHandler {

    private final CommentRepository commentRepository;
    private final PostRedisRepository postRedisRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final PostRedisMapper postRedisMapper;
    private final PostMapper postMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.data.redis.comment-max-size}")
    private int commentsMaxSize;

    @Transactional
    @KafkaHandler
    public void handle(CommentEvent commentEvent) {
        Long commentId = commentEvent.getCommentId();
        Long postId = commentEvent.getPostId();
        log.info("Received event CommentHandlerResponse: {} {}", commentId, commentEvent);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment with id {" + commentId + "} not found"));
        CommentDto commentDto = commentMapper.toDto(comment);

        String redisKey = "post:" + postId;

        boolean success = redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(@NonNull RedisOperations operations) {
                operations.watch(redisKey);

                PostRedis postRedis = postRedisRepository.findById(postId).orElse(null);
                if (postRedis == null) {
                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new NoSuchElementException("Post with id {" + postId + "} not found"));
                    PostDto postDto = postMapper.toPostDto(post);
                    postRedis = postRedisMapper.toPostCache(postDto);
                    postRedisRepository.save(postRedis);
                }
                addComment(postRedis, commentDto);
                log.info("Comment with id:{} is successfully added to post.", commentId);

                operations.multi();
                postRedisRepository.save(postRedis);

                List<Object> results = operations.exec();

                return results != null;
            }
        });

        if (success) {
            log.info("Comment {} successfully added to post {}", commentId, postId);
        } else {
            log.warn("Failed to update post {} due to concurrent modification, retrying...", postId);
            handle(commentEvent);
        }
    }

    public void addComment(PostRedis postRedis, CommentDto commentDto) {
        List<Long> comments = postRedis.getCommentsIds();

        if (comments == null) {
            comments = new ArrayList<>();
            postRedis.setCommentsIds(comments);
        }
        if (comments.size() == commentsMaxSize) {
            comments.remove(comments.size() - 1);
        }

        comments.add(0, commentDto.getId());
    }
}


