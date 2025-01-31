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

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic-name.like-topic}")
    private String likeTopic;

    @Value("${spring.kafka.partitions-count}")
    private int partitionCount;

    @Value("${spring.kafka.replicas-count}")
    private short replicasCount;

    @Value("${spring.kafka.topic-name.comment-topic}")
    private String commentTopic;

    @Value("${spring.kafka.topic-name.posts-topic}")
    private String postsTopic;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likesTopic() {
        return new NewTopic(likeTopic, partitionCount, replicasCount);
    }

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(commentTopic, partitionCount, replicasCount);
    }

    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(postsTopic, partitionCount, replicasCount);
    }
}
