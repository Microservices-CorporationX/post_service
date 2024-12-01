package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostKafkaConsumer extends AbstractKafkaConsumer<PostPublishedKafkaEvent> {
    private final FeedService feedService;

    @Override
    @KafkaListener(topics = "${kafka.topics.post}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, PostPublishedKafkaEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);
        acknowledgment.acknowledge();
    }

    @Override
    protected void processEvent(PostPublishedKafkaEvent event) {
        feedService.addPost(event);
    }
}
