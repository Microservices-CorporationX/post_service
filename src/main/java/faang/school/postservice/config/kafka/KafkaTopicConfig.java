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
    @Value(value = "${spring.kafka.topic.posts:posts}")
    private String POSTS_TOPIC;
    @Value(value = "${spring.kafka.topic.likes:likes}")
    private String LIKES_TOPIC;
    @Value(value = "${spring.kafka.topic.comments:comments}")
    private String COMMENTS_TOPIC;
    @Value(value = "${spring.kafka.topic.post_views:post-views}")
    private String POST_VIEWS_TOPIC;
    @Value(value = "${spring.kafka.topic.feed_heater:feed-heater}")
    private String FEED_HEATER_TOPIC;
    @Value(value = "${spring.kafka.bootstrap-servers:localhost:29092}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPosts() {
        return new NewTopic(POSTS_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topicLikes() {
        return new NewTopic(LIKES_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topicComments() {
        return new NewTopic(COMMENTS_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topicPostViews() {
        return new NewTopic(POST_VIEWS_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic topicFeedHeater() {
        return new NewTopic(FEED_HEATER_TOPIC, 1, (short) 1);
    }
}
