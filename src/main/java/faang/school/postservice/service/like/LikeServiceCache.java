package faang.school.postservice.service.like;

import faang.school.postservice.mapper.like.LikeCacheMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.cache.LikeCache;
import faang.school.postservice.producer.KafkaLikeProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceCache {
    private final KafkaLikeProducer likeProducer;
    private final LikeCacheMapper likeCacheMapper;

    public void sendEvent(Like like) {
        LikeCache likeCache = likeCacheMapper.toCache(like);
        likeProducer.send(likeCache);
    }
}
