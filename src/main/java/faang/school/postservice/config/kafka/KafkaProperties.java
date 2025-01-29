package faang.school.postservice.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KafkaProperties {

  @Value("${spring.data.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Value("${spring.data.kafka.topic.posts}")
  private String postsTopic;

}