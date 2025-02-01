package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.model.cache.LikeCache;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends KafkaAbstractProducer<LikeCache> {

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Value("${spring.data.kafka.topics.like_topic}")
    private String likeTopic;

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name(likeTopic).build();
    }


    public void send(LikeCache like) {
        super.sendMessage(likeTopic, like);
    }
}
