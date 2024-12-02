package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value(value = "${spring.kafka.topic.post-publisher}")
    private String publishPostTopicName;
    @Value(value = "${spring.kafka.topic.comment-publisher}")
    private String publishCommentTopicName;
    @Value(value = "${spring.kafka.topic.like-publisher}")
    private String publishLikeTopicName;
    @Value(value = "${spring.kafka.topic.view-publisher}")
    private String publishViewTopicName;
    @Value(value = "${spring.kafka.topic.heat-feed-publisher}")
    private String publishHeatFeedBatchTopicName;

    @Bean
    public NewTopic publishPostTopic() {
        return new NewTopic(publishPostTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic publishCommentTopic() {
        return new NewTopic(publishCommentTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic likePostTopic() {
        return new NewTopic(publishLikeTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic publishPostViewTopic() {
        return new NewTopic(publishViewTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic publishHeatFeedBatchTopic() {
        return new NewTopic(publishHeatFeedBatchTopicName, 1, (short) 1);
    }
}
