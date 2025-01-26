package faang.school.postservice.listener.kafka;

import faang.school.postservice.events.LikeEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LikeEventListener {

    @KafkaListener(topics = "${spring.kafka.topic-name.comments}", groupId = "1")
    public void listenLikeEvent(LikeEvent likeEvent){

    }
}
