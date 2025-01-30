package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    @Min(1)
    private Long id;
    @Min(1) @NotNull(message = "User id is required")
    private Long userId;
    @Min(1)
    private Long postId;
    @Min(1)
    private Long commentId;
    private LocalDateTime createdAt;
}