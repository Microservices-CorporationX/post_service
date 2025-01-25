package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentRedisDto implements Comparable<CommentRedisDto> {

    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
    private List<Long> likes;

    @Override
    public int compareTo(CommentRedisDto o) {
        return this.createdAt.compareTo(o.createdAt);
    }
}
