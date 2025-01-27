package faang.school.postservice.listener;

import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventListenerKafka {
    @KafkaListener(groupId = "posts-group", topics = "posts", containerFactory = "postsKafkaListenerContainerFactory")
    void listener(Post post) {
        log.info("Received message [{}] in posts-group", post);
    }
}
