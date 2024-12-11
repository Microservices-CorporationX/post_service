package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    @Positive(message = "User id should be a positive number")
    private Long userId;
    @Positive(message = "Comment id should be a positive number")
    private Long commentId;
}
