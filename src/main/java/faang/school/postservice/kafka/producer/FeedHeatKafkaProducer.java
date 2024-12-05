package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FeedHeatKafkaProducer extends AbstractKafkaProducer<PostPublishedKafkaEvent> {

    @Value("${kafka.topics.feed-heat}")
    private String feedHeatKafkaTopic;

    public FeedHeatKafkaProducer(KafkaTemplate<String, PostPublishedKafkaEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return feedHeatKafkaTopic;
    }
}

