package faang.school.postservice.listener.kafka;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.events.PostViewEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PostViewListener {
    @KafkaListener(topics = "${spring.kafka.topic-name.post}", groupId = "1")
    public void listenPostEvent(PostViewEvent postViewEvent){

    }
}
