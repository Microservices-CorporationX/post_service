package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {

  private final Logger logger =
      LoggerFactory.getLogger(this.getClass());

  @KafkaListener(topics = "post_topic", containerFactory = "kafkaListenerContainerFactory", groupId = "feed-group-id")
  public void handle(PostEventDto dto) {

    logger.info("MESSAGE SUCCESSFULLY RECEIVED BY CONSUMER. BUT NOT PROCESSED YET");

  }

}
