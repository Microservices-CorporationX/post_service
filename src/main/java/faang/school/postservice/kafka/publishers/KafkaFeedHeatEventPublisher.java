package faang.school.postservice.kafka.publishers;

import faang.school.postservice.kafka.kafka_events_dtos.AbstractKafkaEventDto;
import faang.school.postservice.kafka.kafka_events_dtos.FeedHeatKafkaEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaFeedHeatEventPublisher extends AbstractEventPublisher {
    @Value(value = "${spring.kafka.keys.feed_heat_topic}")
    private String kafkaFeedHeatKey;

    public KafkaFeedHeatEventPublisher(
            KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate,
            @Qualifier("feedHeatKafkaTopic") NewTopic feedHeatEventTopic) {
        super(kafkaTemplate, feedHeatEventTopic);
    }

    public void sendFeedHeatingEvent(FeedHeatKafkaEventDto heatEventDto) {
        sendEvent(heatEventDto, kafkaFeedHeatKey);
    }
}