package faang.school.postservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaConfigProducer {

    @Value("${spring.data.kafka.topics.comments.name}")
    private String commentsTopic;

    @Value("${spring.data.kafka.topics.comments.partitions}")
    private int partitionCount;

    @Value("${spring.data.kafka.topics.comments.replicas}")
    private int replicaCount;

    @Value("${spring.data.kafka.topics.comments.configs.min-insync-replicas}")
    private String configsValue;

    @Bean
    NewTopic createCommentTopic() {
        return TopicBuilder.name(commentsTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .configs(Map.of("min.insync.replicas", configsValue))
                .build();
    }
}
