package faang.school.postservice.kafka_publishers;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka_events.PostKafkaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostPublisher {
    private final KafkaTemplate<String, PostKafkaEventDto> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    @Value(value = "${spring.kafka.topics_names}")
    private String postsTopicName;

    public void sendPostEvent(PostKafkaEventDto postEvent) {
        try {
            List<Long> postAuthorFollowers = fetchPostAuthorFollowers(postEvent.getAuthorId());
            postEvent.setAuthorFollowersIds(postAuthorFollowers);

            CompletableFuture<SendResult<String, PostKafkaEventDto>> future = kafkaTemplate.send(
                    postsTopicName,
                    postEvent.getPostId().toString(),
                    postEvent
            );

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Successfully sent PostCache with ID [{}] to topic [{}] at offset [{}]",
                            postEvent.getPostId(), postsTopicName, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send PostCache [{}] due to error", postEvent, exception);
                }
            });

        } catch (Exception e) {
            log.error("Error processing post event [{}]: {}", postEvent, e.getMessage(), e);
        }
    }

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 3000)
    )
    protected List<Long> fetchPostAuthorFollowers(Long authorId) {
        return Optional.ofNullable(userServiceClient.getFollowersIds(authorId))
                .orElseGet(Collections::emptyList);
    }

    @Recover
    public List<Long> recoverFetchPostAuthorFollowers(Exception e, Long authorId) {
        log.error("Failed to fetch followers for author [{}] after retries. Returning empty list. Error: {}",
                authorId, e.getMessage(), e);
        return Collections.emptyList();
    }
}