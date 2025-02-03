package faang.school.postservice.consumer;

import faang.school.postservice.model.cache.PostEvent;
import faang.school.postservice.model.cache.PostViewEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostViewConsumer extends SimpleAbstractConsumer<PostViewEvent> {

    public KafkaPostViewConsumer(RedisPostRepository redisPostRepository) {
        super(redisPostRepository);
    }

    public void listen(PostViewEvent postViewEvent, Acknowledgment acknowledgment) {
        listenEvent(postViewEvent, acknowledgment);
    }

    @Override
    protected void processEvent(PostViewEvent event, PostEvent post) {
        int currentCountOfViews = post.getViews();
        post.setViews(currentCountOfViews + 1);

        log.debug("Increased number of views on post {}. Total views: {}", post.getId(), post.getViews());

    }
}

