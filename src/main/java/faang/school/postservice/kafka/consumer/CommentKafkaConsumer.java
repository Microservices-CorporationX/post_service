package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentKafkaConsumer extends AbstractKafkaConsumer<CommentSentKafkaEvent> {
    private final RedisPostService redisPostService;

    @Override
    @KafkaListener(topics = "${kafka.topics.comment}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, CommentSentKafkaEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);
        acknowledgment.acknowledge();
    }

    @Override
    protected void processEvent(CommentSentKafkaEvent event) {
        redisPostService.addComment(event);
    }
}
