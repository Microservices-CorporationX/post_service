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
public class FeedHeatKafkaConsumer extends AbstractKafkaConsumer<PostPublishedKafkaEvent> {
    private final FeedService feedService;

    @Override
    @KafkaListener(
            topics = "${kafka.topics.feed-heat}",
            groupId = "${kafka.consumer.groups.feed-heat.group-id}",
            concurrency = "${kafka.consumer.groups.feed-heat.concurrency}"
    )
    public void consume(ConsumerRecord<String, PostPublishedKafkaEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);
        acknowledgment.acknowledge();
    }

    @Override
    protected void processEvent(PostPublishedKafkaEvent event) {
        feedService.addPost(event);
    }
}
