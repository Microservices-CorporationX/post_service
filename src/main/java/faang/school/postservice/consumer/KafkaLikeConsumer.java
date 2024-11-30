package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.KafkaLikeDto;
import faang.school.postservice.service.post.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic.likes:likes}")
    public void listener(KafkaLikeDto event, Acknowledgment acknowledgment) {
        try {
            postCacheService.incrementPostLikes(event.getPostId(), event.getId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Like is not added to post with id: " + event.getPostId());
            throw e;
        }
    }
}
