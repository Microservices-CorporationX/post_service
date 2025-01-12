package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig extends DefaultKafkaConfig {
    @Value(value = "${spring.kafka.topic.posts:posts}")
    private String postsTopic;
    @Value(value = "${spring.kafka.topic.likes:likes}")
    private String likesTopic;
    @Value(value = "${spring.kafka.topic.comments:comments}")
    private String commentsTopic;
    @Value(value = "${spring.kafka.topic.post_views:post-views}")
    private String postViewsTopic;
    @Value(value = "${spring.kafka.topic.feed_heater:feed-heater}")
    private String feedHeaterTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPosts() {
        return TopicBuilder.name(postsTopic).build();
    }

    @Bean
    public NewTopic topicLikes() {
        return TopicBuilder.name(likesTopic).build();
    }

    @Bean
    public NewTopic topicComments() {
        return TopicBuilder.name(commentsTopic).build();
    }

    @Bean
    public NewTopic topicPostViews() {
        return TopicBuilder.name(postViewsTopic).build();
    }

    @Bean
    public NewTopic topicFeedHeater() {
        return TopicBuilder.name(feedHeaterTopic).build();
    }
}
