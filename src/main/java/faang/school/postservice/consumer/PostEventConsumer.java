package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostFeedEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventConsumer {

    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topic.post.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${spring.kafka.consumer.concurrency}")
    public void listenEvent(String message, Acknowledgment acknowledgment){
        try{
            PostFeedEvent event = objectMapper.readValue(message, PostFeedEvent.class);
            feedService.addPostToFeed(event);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Message processing error: {}", message, e);
        }
    }
}
