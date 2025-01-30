package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class FeedPost implements Comparable<FeedPost>, Serializable {

    private Long postId;

    @Override
    public int compareTo(FeedPost o) {
        return Long.compare(o.postId, postId);
    }
}
