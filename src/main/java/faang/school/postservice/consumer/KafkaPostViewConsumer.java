package faang.school.postservice.consumer;

import faang.school.postservice.model.cache.PostViewEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

public class KafkaPostViewConsumer {

    @KafkaListener(topics = "${spring.data.kafka.topics.post_view_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(PostViewEvent postViewEvent, Acknowledgment acknowledgment) {

    }
}
