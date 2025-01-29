package faang.school.postservice.controller.news.feed;

import faang.school.postservice.config.kafka.TestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsFeedController {

  private final Logger logger =
      LoggerFactory.getLogger(this.getClass());

  private final KafkaTemplate<String, Object> template;

  @GetMapping("/hello")
  public String hello() throws Exception {
    String topicName = "posts";
    template.send(topicName, new TestDto("Message To Broker", 123));

    logger.info("All messages received");
    return "Hello Kafka!";
  }

}
