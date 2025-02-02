package faang.school.postservice.kafka.publishers;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.kafka_events_dtos.AbstractKafkaEventDto;
import faang.school.postservice.kafka.kafka_events_dtos.PostKafkaEventDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class KafkaPostEventPublisher extends AbstractEventPublisher{
    @Value(value = "${spring.kafka.keys.post_topic}")
    private String kafkaPostKey;
    private final UserServiceClient userServiceClient;

    public KafkaPostEventPublisher(
            KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate,
            @Qualifier("postTopic")NewTopic postTopic,
            UserServiceClient userServiceClient
    ){
        super(kafkaTemplate, postTopic);
        this.userServiceClient = userServiceClient;
    }

    public void sendPostEvent(PostKafkaEventDto postEvent) {
            List<Long> postAuthorFollowers = fetchPostAuthorFollowers(postEvent.getAuthorId());
            postEvent.setAuthorFollowersIds(postAuthorFollowers);
            sendEvent(postEvent, kafkaPostKey);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000))
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