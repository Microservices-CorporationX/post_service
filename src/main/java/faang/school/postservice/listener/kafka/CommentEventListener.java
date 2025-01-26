package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.CommentDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CommentEventListener {
    @KafkaListener(topics = "${spring.kafka.topic-name.comments}", groupId = "1")
    public void listenGroupFoo(CommentDto comment) {
        System.out.println("Received Message in group foo: " + comment.toString());
    }
}
