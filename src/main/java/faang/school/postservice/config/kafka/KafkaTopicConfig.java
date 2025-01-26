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

    @Value("${kafka.topics.post-view.name}")
    private String postViewTopicName;
    @Value("${kafka.topics.post-view.partitions}")
    private int postViewTopicPartitions;
    @Value("${kafka.topics.post-view.replication-factor}")
    private int postViewTopicReplicationFactor;

    @Value("${kafka.topics.post-publish.name}")
    private String postPublishTopicName;
    @Value("${kafka.topics.post-publish.partitions}")
    private int postPublishTopicPartitions;
    @Value("${kafka.topics.post-publish.replication-factor}")
    private int postPublishTopicReplicationFactor;

    @Value("${kafka.topics.post-comment.name}")
    private String postCommentTopicName;
    @Value("${kafka.topics.post-comment.partitions}")
    private int postCommentTopicPartitions;
    @Value("${kafka.topics.post-comment.replication-factor}")
    private int postCommentTopicReplicationFactor;

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

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(postViewTopicName, postViewTopicPartitions, (short) postViewTopicReplicationFactor);
    }

    @Bean
    public NewTopic postPublishTopic() {
        return new NewTopic(postPublishTopicName, postPublishTopicPartitions, (short) postPublishTopicReplicationFactor);
    }

    @Bean
    public NewTopic postCommentTopic() {
        return new NewTopic(postCommentTopicName, postCommentTopicPartitions, (short) postCommentTopicReplicationFactor);
    }
}
