package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.PostViewEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.views.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(PostViewEvent postViewEvent) {
        log.info("Received post view event {}. Processing...", postViewEvent);
        newsFeedService.incrementPostViewCount(postViewEvent);
    }
}
