package faang.school.postservice.config.kafka;

import faang.school.postservice.event.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.properties.delivery-timeout-ms}")
    private String deliveryTimeout;

    @Value("${spring.kafka.producer.properties.linger-ms}")
    private String linger;

    @Value("${spring.kafka.producer.properties.request-timeout-ms}")
    private String requestTimeout;

    @Value("${spring.kafka.producer.properties.enable-idempotence}")
    private boolean idempotence;

    @Value("${spring.kafka.producer.properties.max-in-flight-requests-per-connection}")
    private String maxInflightRequests;

    @Value("${spring.kafka.channels.post-channel.partitions}")
    private int partitions;

    @Value("${spring.kafka.channels.post-channel.replicasCount}")
    private int replicasCount;

    @Value("${spring.kafka.channels.post-channel.min-insync-replicas}")
    private String minInsyncReplicas;

    @Value("${spring.kafka.channels.post-channel.name}")
    private String postChannel;

//    private final Environment environment;

    @Bean
    Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        config.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInflightRequests);

        return config;
    }
//
//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, server);
//        return new KafkaAdmin(configs);
//    }

    @Bean
    ProducerFactory<String, PostPublishedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, PostPublishedEvent> kafkaTemplate() {
        return new KafkaTemplate<String, PostPublishedEvent>(producerFactory());
    }

    @Bean
    NewTopic postKafkaTopic() {
        return TopicBuilder
                .name(postChannel)
                .partitions(partitions)
                .replicas(replicasCount)
                .configs(Map.of("min.insync.replicas", minInsyncReplicas))
                .build();
    }
}
