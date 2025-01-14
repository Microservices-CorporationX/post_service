package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.KafkaPostDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topic.posts:posts}")
    public void listener(KafkaPostDto event, Acknowledgment acknowledgment) {
        try {
            feedService.addPostIdToAuthorSubscribers(event.getPostId(), event.getSubscriberIds());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post with id:{} is not added to subscribers feeds.", event.getPostId());
            throw e;
        }
    }
}
