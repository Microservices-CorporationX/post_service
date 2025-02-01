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
public class KafkaGeneralConfig {

    @Value(value = "${spring.kafka.port}")
    private String port;
    @Value(value = "${spring.kafka.host}")
    private String host;
    @Value(value = "${spring.kafka.topics_names.post_topic}")
    private String postsTopicName;
    @Value(value = "${spring.kafka.topics_names.post_view_topic}")
    private String postsViewTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, host + ":" + port);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(postsTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic postViewKafkaTopic() {
        return new NewTopic(postsViewTopicName, 1, (short) 1);
    }

}