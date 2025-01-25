package faang.school.postservice.config.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentsTopicConfig {
    @Value("${spring.data.kafka.topics.comments.name}")
    private String topicName;
    @Value("${spring.data.kafka.topics.comments.partitions}")
    private int partitions;
    @Value("${spring.data.kafka.topics.comments.replicas}")
    private short replicas;

    @Bean
    @Qualifier("comments")
    public NewTopic createCommentsTopic() {
        return new NewTopic(topicName, partitions, replicas);
    }
}
