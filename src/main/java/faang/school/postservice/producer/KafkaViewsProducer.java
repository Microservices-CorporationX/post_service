package faang.school.postservice.producer;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.events.PostViewsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaViewsProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.views-topic}")
    private String viewsTopic;

    public void sendViews(PostViewsEvent postViewsEvent) {
        kafkaTemplate.send(viewsTopic, postViewsEvent);
    }
}
