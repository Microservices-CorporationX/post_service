package faang.school.postservice.consumer.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.event.LikePostEvent;
import faang.school.postservice.repository.CustomPostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostLikeConsumer {
    private final ObjectMapper objectMapper;
    private final CustomPostCacheRepository customPostCacheRepository;

    @KafkaListener(topics = "${kafka.topics.post-like.name}",
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message, Acknowledgment ack) {
        try {
            LikePostEvent event = objectMapper.readValue(message, LikePostEvent.class);
            customPostCacheRepository.incrementLikes(event.getPostId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Could not deserialize LikePostEvent from json", e);
            throw new IllegalStateException("Could not deserialize LikePostEvent from json");
        }
    }
}
