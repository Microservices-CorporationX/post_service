package faang.school.postservice.consumer;

import faang.school.postservice.config.redis.JedisConfig;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.redis.repository.PostCacheRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@EnableKafka
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final PostCacheRepository postCacheRepository;
    private final PostService postService;
    private final PostMapper postMapper;
    private final JedisConfig redisConfig;

    @KafkaListener(topics = "comments", groupId = "post-service")
    public void listen(CommentDto commentDto, Acknowledgment ack) {
        long postId = commentDto.getPostId();

        PostDto post = postCacheRepository.getPostBy(postId);

        if (post != null) {
            List<CommentDto> tempCommentsDto = post.getComments();
            if (tempCommentsDto.size() < redisConfig.getMaxComments()) {
                tempCommentsDto.add(commentDto);
            } else {
                tempCommentsDto = tempCommentsDto.stream()
                        .sorted(Comparator.comparing(CommentDto::getCreatedAt))
                        .limit(redisConfig.getMaxComments())
                        .toList();
            }
            post.setComments(tempCommentsDto);

            postCacheRepository.cachePost(post);
            log.info("Comment successfully added to post with ID: {}", postId);
        } else {
            postService.findPostById(postId)
                    .ifPresentOrElse(
                            p -> {
                                PostDto postDto = postMapper.toDto(p);
                                postDto.getComments().add(commentDto);
                                postCacheRepository.cachePost(postDto);
                                log.info("Comment successfully added to post with ID: {}", postId);
                                log.info("Post with ID: {} updated in cache", postId);
                            },
                            () -> log.warn("Post with ID: {} not found in DB", postId)
                    );
        }
        ack.acknowledge();
    }
}
