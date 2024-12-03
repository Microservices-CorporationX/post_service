package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewKafkaConsumer extends AbstractKafkaConsumer<PostViewKafkaEvent> {
    private final RedisPostService redisPostService;

    @Override
    @KafkaListener(
            topics = "${kafka.topics.post-view}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    public void consume(ConsumerRecord<String, PostViewKafkaEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);
        acknowledgment.acknowledge();
    }

    @Override
    protected void processEvent(PostViewKafkaEvent event) {
        redisPostService.incrementPostViewsWithTransaction(event.getPostId(), event.getViewerId(), event.getViewDateTime());
    }
}
