package faang.school.postservice.service.feed;

import faang.school.postservice.dto.event.PostFeedEvent;
import faang.school.postservice.repository.redis.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedCacheRepository feedRepository;

    @Value("${spring.data.redis.feed.key-prefix}")
    private String feedPrefix;

    public void addPostToFeed(PostFeedEvent event) {
        double score = event.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//TODO
        event.getSubscribersIds().forEach(subscriberId ->{
                    String feedKKey = feedPrefix + subscriberId;
                    feedRepository.bindPostToFollower(feedKKey, event.getPostId(), score);
                });
    }

}
