package faang.school.postservice.producer;

import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.event.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaProperties kafkaProperties;

  public void sendEvent(PostEventDto event) {
    log.info("Sending New Post Event with Followers to Kafka");
    kafkaTemplate.send(kafkaProperties.getPostsTopic(), event);
  }
}