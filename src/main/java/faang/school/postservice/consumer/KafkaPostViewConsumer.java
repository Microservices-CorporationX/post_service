package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.KafkaPostViewDto;
import faang.school.postservice.service.post.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic.post_views:post-views}")
    public void listener(KafkaPostViewDto event, Acknowledgment acknowledgment) {
        try {
            postCacheService.addPostView(event.postId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("PostView on post with id:{} is not added to post.", event.postId());
            throw e;
        }
    }
}
