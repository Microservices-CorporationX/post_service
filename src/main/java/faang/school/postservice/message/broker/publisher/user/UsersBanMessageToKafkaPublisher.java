package faang.school.postservice.message.broker.publisher.user;

import faang.school.postservice.dto.user.message.UsersForBanMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UsersBanMessageToKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.user.ban}")
    private String topicName;

    public void publish(UsersForBanMessage message) {
        kafkaTemplate.send(topicName, message);
    }
}
