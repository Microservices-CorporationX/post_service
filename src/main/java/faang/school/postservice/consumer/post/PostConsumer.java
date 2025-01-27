package faang.school.postservice.consumer.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.event.PublishPostEvent;
import faang.school.postservice.publisher.postpublish.PublishPostEventPublisher;
import faang.school.postservice.repository.feed.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PostConsumer {
    private final ObjectMapper objectMapper;
    private final PublishPostEventPublisher publishPostEventPublisher;
    private final FeedRepository feedRepository;

    @Value("${kafka.listeners.post-listener.batch-size}")
    private int batchSize;

    @KafkaListener(topics = {"${kafka.topics.post-publish.name}", "${kafka.topics.post-publish-batched.name}"},
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message, Acknowledgment ack) {
        try {
            PublishPostEvent publishPostEvent = objectMapper.readValue(message, PublishPostEvent.class);
            if (publishPostEvent.getFollowers().size() > batchSize) {
                sendBatchesAsEvents(publishPostEvent);
            } else {
                log.info("Processing publish post event {}", publishPostEvent);
                addPostToFollowersFeed(publishPostEvent);
            }
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Error when deserializing PublishPostEvent", e);
            throw new IllegalStateException("Could not deserialize PublishPostEvent");
        }
    }

    private void sendBatchesAsEvents(PublishPostEvent publishPostEvent) {
        log.info("Breaking publish post event into batches, num of followers = {}", publishPostEvent.getFollowers().size());
        ListUtils.partition(publishPostEvent.getFollowers(), batchSize)
                .forEach(batch -> {
                    PublishPostEvent postEvent = PublishPostEvent
                            .builder()
                            .postId(publishPostEvent.getPostId())
                            .followers(batch)
                            .publishedAt(publishPostEvent.getPublishedAt())
                            .build();
                    publishPostEventPublisher.publish(postEvent, true);
                });
    }

    private void addPostToFollowersFeed(PublishPostEvent event) {
        event.getFollowers().forEach(followerId -> feedRepository.addPostToUserFeed(
                followerId,
                event.getPostId(),
                event.getPublishedAt()
        ));
    }
}
