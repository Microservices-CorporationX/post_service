package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${spring.kafka.topic.posts.name}")
    private String postsTopicName;

    @Value("${spring.kafka.topic.posts.partitions}")
    private int postsPartitionsAmount;

    @Value("${spring.kafka.topic.comments.name}")
    private String commentsTopicName;

    @Value("${spring.kafka.topic.comments.partitions}")
    private int commentsPartitionsAmount;

    @Value("${spring.kafka.topic.likes.name}")
    private String likesTopicName;

    @Value("${spring.kafka.topic.likes.partitions}")
    private int likesPartitionsAmount;

    @Value("${spring.kafka.topic.views.name}")
    private String viewsTopicName;

    @Value("${spring.kafka.topic.views.partitions}")
    private int viewsPartitionsAmount;

    @Value("${spring.kafka.topic.heat.name}")
    private String heatTopicName;

    @Value("${spring.kafka.topic.heat.partitions}")
    private int heatPartitionsAmount;

    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name(postsTopicName)
                .partitions(postsPartitionsAmount)
                .build();
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(commentsTopicName)
                .partitions(commentsPartitionsAmount)
                .build();
    }

    @Bean
    public NewTopic likesTopic() {
        return TopicBuilder.name(likesTopicName)
                .partitions(likesPartitionsAmount)
                .build();
    }

    @Bean
    public NewTopic viewsTopic() {
        return TopicBuilder.name(viewsTopicName)
                .partitions(viewsPartitionsAmount)
                .build();
    }

    @Bean
    public NewTopic heatTopic() {
        return TopicBuilder.name(heatTopicName)
                .partitions(heatPartitionsAmount)
                .build();
    }
}
