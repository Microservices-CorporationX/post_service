package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.event.PostPublishedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserContext userContext;

    @Async("executorKafkaSend")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPostEvent(Post post, long contextUserId) {
        userContext.setUserId(contextUserId);
        List<Long> followerIds = userServiceClient.getFollowersByAuthorNF(post.getAuthorId());
        if (followerIds.isEmpty()) {
            log.info("No followers found for post {}", post);
            return;
        }
        PostPublishedEvent event = PostPublishedEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .followersId(followerIds)
                .build();
        kafkaPostProducer.publish(event);
    }

    @Override
    public void sendPostViewEvent() {
        //TODO
    }

    @Override
    public void sendLikeEvent() {
        //TODO
    }

    @Override
    public void sendCommentEvent() {
        //TODO
    }

}
