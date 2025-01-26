package faang.school.postservice.consumer;

import faang.school.postservice.event.PostPublishedEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topics.post-channel.name}")
    public void consume(PostPublishedEvent event, Acknowledgment ack) {
        log.info("Received post published event: {}", event);

        Flux.fromIterable(event.getFollowersId())
                .flatMap(followerId -> feedService.bindPostToFollower(followerId, event.getPostId()))
                .doOnComplete(() -> {
                    log.info("Kafka post event consumer finished for post with id {}", event.getPostId());
                    ack.acknowledge();
                })
                .doOnError(ex -> log.error("Error processing post with id {}", event.getPostId(), ex))
                .subscribe();
    }
}
