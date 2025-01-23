package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

public record RedisCommentDto(long id,
                              Long authorId,
                              int likeCount,
                              String content,
                              LocalDateTime createdAt
) {
}
