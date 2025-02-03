package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CacheCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("Post")
public class PostCache implements Serializable {

    @Id
    private Long id;

    private String content;
    private Long authorId;
    private Long views;
    private Long likes;
    private List<CacheCommentDto> comments;
    private LocalDateTime publishedAt;
}