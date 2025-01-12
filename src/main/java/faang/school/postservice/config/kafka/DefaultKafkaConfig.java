package faang.school.postservice.config.kafka;

import org.springframework.beans.factory.annotation.Value;

public class DefaultKafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers:localhost:29092}")
    protected String bootstrapAddress;
}