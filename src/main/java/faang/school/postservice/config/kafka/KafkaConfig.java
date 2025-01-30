package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    private final Environment environment;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.data.kafka.producer.bootstrap-servers"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        configProps.put(ProducerConfig.ACKS_CONFIG, environment.getRequiredProperty("spring.data.kafka.producer.acks"));
//        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
//                environment.getProperty("spring.data.kafka.producer.properties.delivery-timeout-ms"));
//        configProps.put(ProducerConfig.LINGER_MS_CONFIG,
//                environment.getProperty("spring.data.kafka.producer.properties.linger-ms"));
//        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
//                environment.getProperty("spring.data.kafka.producer.properties.request-timeout-ms"));
//        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
//                environment.getProperty("spring.data.kafka.producer.properties.enable-idempotence"));
//        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
//                environment.getProperty("spring.data.kafka.producer.properties.max-in-flight-requests-per-connection"));

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.data.kafka.producer.bootstrap-servers"));
        return new KafkaAdmin(configs);
    }

    @Bean
    @Qualifier(value ="postsTopic")
    public NewTopic postsTopic() {
        return newTopicBuilder(environment.getRequiredProperty("spring.data.kafka.topics.post-channel.name"));
    }

    @Bean
    @Qualifier(value ="commentsTopic")
    public NewTopic commentsTopic() {
        return newTopicBuilder(environment.getRequiredProperty("spring.data.kafka.topics.comment-channel.name"));
    }


    private NewTopic newTopicBuilder(String topicName) {
        return TopicBuilder
                .name(topicName)
//                .partitions(Integer.parseInt(environment.getRequiredProperty("spring.data.kafka.topics.partitions")))
//                .replicas(Integer.parseInt(environment.getRequiredProperty("spring.data.kafka.topics.replicasCount")))
//                .configs(Map.of(
//                        "min.insync.replicas",
//                        environment.getRequiredProperty("spring.data.kafka.topics.min-insync-replicas")))
                .build();
    }
}
