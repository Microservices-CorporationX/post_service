package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public <V> ProducerFactory<String, V> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public <V> KafkaTemplate<String, V> kafkaTemplate(ProducerFactory<String, V> producerFactory, ObjectMapper objectMapper) {
        KafkaTemplate<String, V> template = new KafkaTemplate<>(producerFactory);
        template.setMessageConverter(new StringJsonMessageConverter(objectMapper));
        return template;
    }

    @Bean
    public NewTopic authorizationTopic() {
        return new NewTopic("post-topic", 1, (short) 1);
    }
}