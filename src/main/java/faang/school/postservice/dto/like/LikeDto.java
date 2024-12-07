package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    @Min(1)
    private Long id;

    @Min(1)
    private Long userId;

    @Min(1)
    private Long commentId;

    @Min(1)
    private Long postId;

    private String createdAt;
}
