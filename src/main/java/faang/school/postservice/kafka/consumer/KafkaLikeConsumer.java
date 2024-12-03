package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer extends AbstractKafkaConsumer<LikeKafkaEvent> {

    private final RedisPostService redisPostService;

    @Override
    public void consume(ConsumerRecord<String, LikeKafkaEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);
        acknowledgment.acknowledge();
    }

    @Override
    @KafkaListener(
            topics = "${kafka.topics.like}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    protected void processEvent(LikeKafkaEvent event) {
        try {
            redisPostService.incrementLikesWithTransaction(event.getPostId(), event.getLikeId());
        } catch (Exception e) {
            log.error("Error incrementing like count for like ID {}: {}", event.getLikeId(), e.getMessage());
            throw e;
        }
    }
}

