package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.posts.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(PostEvent postEvent) {
        log.info("Received post event {}. Processing...", postEvent);
        newsFeedService.addPostToFollowersFeedInCache(postEvent);
    }
}
