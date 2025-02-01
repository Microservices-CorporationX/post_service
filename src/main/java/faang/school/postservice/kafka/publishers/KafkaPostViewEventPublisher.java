package faang.school.postservice.kafka.publishers;

import faang.school.postservice.kafka.kafka_events_dtos.AbstractKafkaEventDto;
import faang.school.postservice.kafka.kafka_events_dtos.PostViewKafkaEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewEventPublisher extends AbstractEventPublisher {
    @Value(value = "${spring.kafka.keys.post_view_topic}")
    private String postViewKey;


    public KafkaPostViewEventPublisher(KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate, @Qualifier("postViewKafkaTopic") NewTopic topic) {
        super(kafkaTemplate, topic);
    }

    public void sendPostViewEvent(PostViewKafkaEventDto eventDto) {
        sendEvent(eventDto, postViewKey);
    }
}