package faang.school.postservice.producer;

import faang.school.postservice.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private final KafkaTemplate<String, CommentDto> kafkaTemplate;

    @Value("${spring.kafka.topic.comment.name}")
    private String commentsTopic;

    public void sendMessage(CommentDto commentDto) {
        log.info("Producing commentDto message to topic: {}", commentsTopic);
        kafkaTemplate.send(commentsTopic, commentDto);
    }
}
