package faang.school.postservice.config.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

  private final KafkaProperties kafkaProperties;

  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
    return new KafkaAdmin(configs);
  }

  @Bean
  NewTopic postsTopic() {
    return TopicBuilder.name(kafkaProperties.getPostsTopic())
        .partitions(3)
//        .replicas(3)
        .build();
  }
}
