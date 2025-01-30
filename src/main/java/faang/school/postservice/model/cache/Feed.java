package faang.school.postservice.model.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
import java.util.TreeSet;

@RedisHash("feed")
@Getter
@Setter
@NoArgsConstructor
public class Feed implements Serializable {

    @Id
    private Long userId;
    private TreeSet<FeedPost> feed = new TreeSet<>();

    public Feed(long userId) {
        this.userId = userId;
    }

    public void addPostToFeed(long postId, int maxFeedSize) {
        feed = feed == null ? new TreeSet<>() : feed;
        feed.add(new FeedPost(postId));
        if (feed.size() > maxFeedSize) {
            feed.pollLast();
        }
    }

    public List<Long> getLastNPosts(int numPosts, Long lastPostId) {
        if (lastPostId == null) {
            return feed.stream()
                    .limit(numPosts)
                    .map(FeedPost::getPostId)
                    .toList();
        }

        return feed.stream()
                .map(FeedPost::getPostId)
                .filter(postId -> postId < lastPostId)
                .limit(numPosts)
                .toList();
    }
}
