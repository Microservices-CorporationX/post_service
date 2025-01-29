package faang.school.postservice.config.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

//TODO @Component failing deserialization
public class Listener {

  private final Logger logger =
      LoggerFactory.getLogger(this.getClass());

  @KafkaListener(topics = "posts", containerFactory = "kafkaListenerContainerFactory", groupId = "my-group-id")
  public void handle(TestDto testDto) {

    logger.info("MESSAGE RECEIVED ************");

  }

}
