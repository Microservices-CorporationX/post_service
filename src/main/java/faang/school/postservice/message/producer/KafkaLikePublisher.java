package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.likes.name}")
    private String likesTopic;

    public void publishLikeEvent(LikeEvent likeEvent) {
        log.info("Sending like event {} into topic {}", likeEvent, likesTopic);
        kafkaTemplate.send(likesTopic, likeEvent);
    }
}
