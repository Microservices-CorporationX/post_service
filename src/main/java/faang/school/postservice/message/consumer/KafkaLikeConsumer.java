package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.LikeEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final NewsFeedService newsFeedService;

    @Value("${spring.kafka.topic.likes.name}")
    private String likesTopic;

    @KafkaListener(topics = "${spring.kafka.topic.likes.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(LikeEvent likeEvent) {
        log.info("Received like event {}. Processing...", likeEvent);
        newsFeedService.incrementLikeCount(likeEvent);
    }
}
