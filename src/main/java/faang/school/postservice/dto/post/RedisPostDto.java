package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record RedisPostDto(
                           Long id,
                           String content,
                           Long authorId,
                           Long projectId,
                           int likeCount,
                           List<RedisCommentDto> recentComments,
                           LocalDateTime publishedAt,
                           LocalDateTime createdAt,
                           Set<Long> hashtagIds
) {
}
