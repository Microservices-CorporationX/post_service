package faang.school.postservice.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class KafkaTopicConfig implements ApplicationRunner {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(Collections.singletonMap(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createTopic("comments", 3, (short) 1);
    }

    public void createTopic(String name, int partitions, short replicationFactor) {
        AdminClient adminClient = adminClient();
        NewTopic newTopic = new NewTopic(name, partitions, replicationFactor);

        try {
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            log.info("Topic created: {}", newTopic.name());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating topic: {}", e.getMessage());
        }
    }
}
