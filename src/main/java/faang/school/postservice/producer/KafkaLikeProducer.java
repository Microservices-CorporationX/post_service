package faang.school.postservice.producer;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.events.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.like-topic}")
    private String likeTopic;

    public void sendLikeEvent(LikeEvent likeEvent) {
        kafkaTemplate.send(likeTopic, likeEvent);
    }
}
