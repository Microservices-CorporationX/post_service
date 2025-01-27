package faang.school.postservice.utils;

import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, Post> postKafkaTemplate;

    public void send(Post post, String topicName) {
        log.info("Sending JSON serializer : {}", post.getId());
        postKafkaTemplate.send(topicName, post);
    }
}
