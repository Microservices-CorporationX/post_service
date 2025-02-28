package ru.corporationx.postservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.corporationx.postservice.dto.like.LikeDto;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaLikeProducer<T extends LikeDto> {

    private final KafkaTemplate<String, LikeDto> kafkaLikeTemplate;

    public void send(LikeDto like) {
        try {
            kafkaLikeTemplate.sendDefault(UUID.randomUUID().toString(), like);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            kafkaLikeTemplate.flush();
        }
    }
}
