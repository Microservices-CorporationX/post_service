package faang.school.postservice.cache_entities;

import faang.school.postservice.dto.comment.CommentResponseDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("Post")
@Data
public class PostCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long postId;
    private String content;
    private Long authorId;
    private String authorName;
    private Integer countLikes;
    private Integer countViews;
    private List<CommentResponseDto> lastThreeComments;
    private LocalDateTime publishedAt;
}
