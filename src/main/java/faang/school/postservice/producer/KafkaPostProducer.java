package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractKafkaProducer<PostEvent> {
    public KafkaPostProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.kafka.topic.posts.name}") String topicName
    ) {
        super(kafkaTemplate, objectMapper, topicName);
    }
}
