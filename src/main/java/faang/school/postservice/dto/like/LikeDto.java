package faang.school.postservice.dto.like;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LikeDto {
    @PositiveOrZero
    private Long Id;

    @PositiveOrZero
    private final Long userId;

    @PositiveOrZero
    private final Long commentId;

    @PositiveOrZero
    private final Long postId;


}
