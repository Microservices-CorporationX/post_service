package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.LikeEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@EnableKafka
@Configuration
@Data
@RequiredArgsConstructor
public class KafkaLikeProducer {
    @Value("${spring.kafka.topics.name}")
    private String nameTopic ;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(LikeEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(nameTopic, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event=[" + event +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.info("Unable to send event=[" +
                        event + "] due to : " + ex.getMessage());
            }
        });
    }
}