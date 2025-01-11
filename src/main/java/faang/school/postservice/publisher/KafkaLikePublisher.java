package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaLikePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.like}")
    private String likesTopic;

    public void publishLikeEvent(long postAuthorId, long likeAuthorId, long postId) {
        LikeEvent event = LikeEvent.builder()
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .postId(postId)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send(likesTopic, event);
    }
}
