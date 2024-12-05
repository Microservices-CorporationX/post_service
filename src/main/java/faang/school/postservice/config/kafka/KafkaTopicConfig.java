package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.post}")
    private String kafkaPostTopic;

    @Value("${kafka.topics.comment}")
    private String kafkaCommentTopic;

    @Value("${kafka.topics.post-view}")
    private String kafkaPostViewTopic;

    @Value("${kafka.topics.like}")
    private String kafkaLikeTopic;

    @Value("${kafka.topics.feed-heat}")
    private String kafkaFeedHeatTopic;

    @Bean
    public NewTopic kafkaPostTopic() {
        return new NewTopic(kafkaPostTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic kafkaCommentTopic() {
        return new NewTopic(kafkaCommentTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic kafkaPostViewTopicTopic() {
        return new NewTopic(kafkaPostViewTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic kafkaLikeTopic() {
        return new NewTopic(kafkaLikeTopic, 3, (short) 1);
    }

    @Bean
    public NewTopic kafkaFeedHeatTopic() {
        return new NewTopic(kafkaFeedHeatTopic, 3, (short) 1);
    }
}
