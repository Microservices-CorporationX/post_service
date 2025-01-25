package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value("${kafka.bootstrap-server}")
    private String bootstrapServer;

    @Value("${kafka.topics.post-like.name}")
    private String postLikeTopicName;
    @Value("${kafka.topics.post-like.partitions}")
    private int postLikeTopicPartitions;
    @Value("${kafka.topics.post-like.replication-factor}")
    private int postLikeTopicReplicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postLikeTopic() {
        return new NewTopic(postLikeTopicName, postLikeTopicPartitions, (short) postLikeTopicReplicationFactor);
    }
}
