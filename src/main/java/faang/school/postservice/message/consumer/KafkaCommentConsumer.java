package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.CommentEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.comments.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(CommentEvent commentEvent) {
        log.info("Received comment event {}. Processing...", commentEvent);
        newsFeedService.saveUserToCacheById(commentEvent.authorId());
        newsFeedService.addCommentToPostCache(commentEvent);
    }
}
