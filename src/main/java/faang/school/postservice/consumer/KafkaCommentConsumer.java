package faang.school.postservice.consumer;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.comment-topic}",
    groupId = "${spring.kafka.listener.topics.comment-topic.group-id}")
    public void listenerCommentsTopic(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        postCacheService.addComment(commentEvent.getPostId(), commentEvent);
        acknowledgment.acknowledge();
    }
}
