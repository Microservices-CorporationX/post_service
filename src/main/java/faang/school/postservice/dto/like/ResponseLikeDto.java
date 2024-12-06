package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ResponseLikeDto {
    private long id;
    private long userId;
    private long postId;
    private long commentId;
    private LocalDateTime createdAt;
}
