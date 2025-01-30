package faang.school.postservice.sender.async;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncPostEventSender {
    private final KafkaPostProducer kafkaPostProducer;
    private final UserServiceClient userServiceClient;

    @Async("postEventSenderThreadPool")
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendPostEvents(Post post) {
        if (post.getAuthorId() == null) {
            return;
        }

        List<Long> followerIds = userServiceClient.getFollowers(post.getAuthorId())
                .stream()
                .map(SubscriptionUserDto::getId)
                .toList();

        PostEvent postEvent = PostEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .followerIds(followerIds)
                .publishedAt(post.getPublishedAt())
                .build();
        log.info("Send post event in kafka: {}", postEvent);
        kafkaPostProducer.sendMessage(postEvent);
    }
}