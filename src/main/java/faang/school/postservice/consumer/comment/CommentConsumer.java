package faang.school.postservice.consumer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.model.CommentCache;
import faang.school.postservice.repository.comment.CommentCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentConsumer {
    private final CommentCacheRepository commentCacheRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.post-comment.name}",
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message, Acknowledgment ack) {
        try {
            CommentEvent commentEvent = objectMapper.readValue(message, CommentEvent.class);
            CommentCache commentCache = new CommentCache(
                    commentEvent.getCommentId(),
                    commentEvent.getAuthorId(),
                    commentEvent.getContent(),
                    commentEvent.getCreatedAt()
            );
            commentCacheRepository.save(commentEvent.getPostId(), commentCache);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Could not deserialize CommentEvent from json", e);
            throw new IllegalStateException("Could not deserialize CommentEvent from json");
        }
    }
}
