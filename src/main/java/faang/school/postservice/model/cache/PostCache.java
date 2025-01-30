package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;

@RedisHash(value = "posts", timeToLive = 86400)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long likeCount;
    private Long viewCount;
    private TreeSet<CommentCache> comments;
    private LocalDateTime createdAt;

    public void addComment(CommentCache comment, int maxComments) {
        if (comments == null) {
            comments = new TreeSet<>();
        }

        comments.add(comment);
        if (comments.size() > maxComments) {
            comments.pollLast();
        }
    }

    public void incrementLikeCount() {
        likeCount++;
    }

    public void incrementViewCount() {
        viewCount++;
    }
}
